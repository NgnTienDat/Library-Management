package com.ou.oulib.service;

import com.ou.oulib.dto.request.AuthorRefRequest;
import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.request.BookFilterRequest;
import com.ou.oulib.dto.request.BookUpdateRequest;
import com.ou.oulib.dto.response.BookDetailResponse;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.dto.response.VerifyBarcodeResponse;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BookCopy;
import com.ou.oulib.entity.Category;
import com.ou.oulib.enums.AuditAction;
import com.ou.oulib.enums.BookCopyStatus;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.enums.ResourceType;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.infras.event.AuditMessage;
import com.ou.oulib.infras.producer.AuditProducer;
import com.ou.oulib.mapper.BookMapper;
import com.ou.oulib.repository.AuthorRepository;
import com.ou.oulib.repository.BookCopyRepository;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.CategoryRepository;
import com.ou.oulib.repository.UserRepository;
import com.ou.oulib.specification.BookSpecification;
import com.ou.oulib.utils.Helper;

import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.PageResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookService {

    BookRepository bookRepository;
    BookCopyRepository bookCopyRepository;
    CategoryRepository categoryRepository;
    AuthorRepository authorRepository;
    BookMapper bookMapper;
    CloudinaryUploadService cloudinaryUploadService;
    UserRepository userRepository;
    AuditProducer auditProducer;

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponse addNewBook(BookCreationRequest request,
            MultipartFile thumbnail) {

        List<String> copyBarcodes = request.getCopyBarcodes() == null
                ? List.of()
                : request.getCopyBarcodes().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(barcode -> !barcode.isBlank())
                        .toList();

        if (copyBarcodes.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOTAL_COPIES);
        }
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new AppException(ErrorCode.BOOK_ALREADY_EXISTS);
        }
        if (copyBarcodes.size() != copyBarcodes.stream().distinct().count()) {
            throw new AppException(ErrorCode.DUPLICATE_BARCODE_IN_REQUEST);
        }
        if (bookCopyRepository.existsByBarcodeIn(copyBarcodes)) {
            throw new AppException(ErrorCode.BARCODE_ALREADY_EXISTS);
        }

        Book book = bookMapper.toBook(request);
        book.initializeCopies(copyBarcodes.size());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        book.setCategory(category);

        List<Author> authors = resolveAuthors(request.getAuthors());
        book.setAuthors(authors);

        List<BookCopy> copies = copyBarcodes.stream()
                .map(barcode -> BookCopy.builder()
                        .barcode(barcode)
                        .status(BookCopyStatus.AVAILABLE)
                        .build())
                .toList();
        copies.forEach(book::addCopy);
        book = bookRepository.save(book);

        String thumbnailKey = null;
        try {
            if (thumbnail != null && !thumbnail.isEmpty()) {
                Helper.validateImage(thumbnail);
                thumbnailKey = cloudinaryUploadService.uploadThumbnail(thumbnail);
                book.setThumbnailUrl(thumbnailKey);
            }

            auditProducer.sendAuditLog(AuditMessage.builder()
                    .userId(getCurrentActorUserId())
                    .action(AuditAction.CREATE.name())
                    .resourceType(ResourceType.BOOK.name())
                    .resourceId(parseToLong(book.getId()))
                    .newValue("{\"bookId\":\"" + book.getId() + "\",\"isbn\":\"" + book.getIsbn() + "\"}")
                    .timestamp(Instant.now())
                    .build());

            // It's not necessary to save the book (thumbnail) again
            // because it's still in the persistence context.
            return bookMapper.toBookResponse(book);

        } catch (Exception ex) {
            // If upload fails then delete the newly created DB record
            bookRepository.delete(book);
            cleanupUploadedFiles(thumbnailKey);
            throw ex;
        }
    }

    private List<Author> resolveAuthors(List<AuthorRefRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<Author> result = new ArrayList<>();
        for (AuthorRefRequest req : requests) {
            if (req.getId() != null && !req.getId().isBlank()) {
                Author author = authorRepository.findById(req.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));
                result.add(author);
            } else {
                if (req.getName() == null || req.getName().isBlank()) {
                    throw new AppException(ErrorCode.INVALID_AUTHOR_NAME);
                }
                Author newAuthor = authorRepository.save(
                        Author.builder()
                                .name(req.getName().trim())
                                .build());
                result.add(newAuthor);
            }
        }
        return result;
    }

    private void cleanupUploadedFiles(String thumbnailKey) {
        if (thumbnailKey != null) {
            try {
                cloudinaryUploadService.delete(thumbnailKey);
            } catch (Exception ex) {
                log.error("Failed to cleanup thumbnail {}", thumbnailKey, ex);
            }
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(int page, int size) {
        BookFilterRequest request = BookFilterRequest.builder()
                .page(page)
                .size(size)
                .build();
        return getBooks(request);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(BookFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Book> books = bookRepository.findAll(BookSpecification.build(request), pageable);
        return PageResponseUtils.build(books, bookMapper::toBookResponse);
    }

    @Transactional(readOnly = true)
    public BookDetailResponse getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return bookMapper.toBookDetailResponse(book);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('LIBRARIAN')")
    public VerifyBarcodeResponse verifyBarcode(String barcode) {
        String normalizedBarcode = barcode == null ? null : barcode.trim();

        BookCopy bookCopy = bookCopyRepository.findByBarcode(normalizedBarcode)
                .orElseThrow(() -> new AppException(ErrorCode.BARCODE_NOT_FOUND));

        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new AppException(ErrorCode.BOOK_COPY_NOT_AVAILABLE);
        }

        return VerifyBarcodeResponse.builder()
                .barcode(bookCopy.getBarcode())
                .bookTitle(bookCopy.getBook().getTitle())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponse updateBook(String id, BookUpdateRequest request, MultipartFile newThumbnail) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        String oldState = "{\"title\":\"" + book.getTitle() + "\",\"active\":" + book.isActive() + "}";

        if (request.getTotalCopies() != null) {
            int newTotal = request.getTotalCopies();
            if (newTotal <= 0) {
                throw new AppException(ErrorCode.INVALID_TOTAL_COPIES);
            }
            int borrowed = book.getTotalCopies() - book.getAvailableCopies();
            if (newTotal < borrowed) {
                throw new AppException(ErrorCode.INVALID_TOTAL_COPIES);
            }
            int difference = newTotal - book.getTotalCopies();
            book.setTotalCopies(newTotal);
            book.setAvailableCopies(book.getAvailableCopies() + difference);
        }
        bookMapper.partialUpdate(request, book);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            book.setCategory(category);
        }

        if (request.getAuthors() != null) {
            List<Author> authors = resolveAuthors(request.getAuthors());
            book.setAuthors(authors);
        }

        if (newThumbnail != null && !newThumbnail.isEmpty()) {
            String oldThumbnail = book.getThumbnailUrl();
            try {
                Helper.validateImage(newThumbnail);
                String uploadedUrl = cloudinaryUploadService.uploadThumbnail(newThumbnail);
                book.setThumbnailUrl(uploadedUrl);

                if (oldThumbnail != null) {
                    try {
                        cleanupUploadedFiles(oldThumbnail);
                    } catch (Exception e) {
                        log.warn("Failed to delete old thumbnail {}", oldThumbnail);
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to upload new thumbnail for book {}", book.getId(), ex);
            }
        }

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(getCurrentActorUserId())
                .action(AuditAction.UPDATE.name())
                .resourceType(ResourceType.BOOK.name())
                .resourceId(parseToLong(book.getId()))
                .oldValue(oldState)
                .newValue("{\"title\":\"" + book.getTitle() + "\",\"active\":" + book.isActive() + "}")
                .timestamp(Instant.now())
                .build());

        return bookMapper.toBookResponse(book);
    }

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public void deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        boolean oldActive = book.isActive();
        book.setActive(false);
        bookRepository.save(book);

        auditProducer.sendAuditLog(AuditMessage.builder()
            .userId(getCurrentActorUserId())
            .action(AuditAction.DELETE.name())
            .resourceType(ResourceType.BOOK.name())
            .resourceId(parseToLong(book.getId()))
            .oldValue("{\"active\":" + oldActive + "}")
            .newValue("{\"active\":" + book.isActive() + "}")
            .timestamp(Instant.now())
            .build());
    }

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public void reactivateBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        boolean oldActive = book.isActive();
        book.setActive(true);
        bookRepository.save(book);

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(getCurrentActorUserId())
                .action(AuditAction.UPDATE.name())
                .resourceType(ResourceType.BOOK.name())
                .resourceId(parseToLong(book.getId()))
                .oldValue("{\"active\":" + oldActive + "}")
                .newValue("{\"active\":" + book.isActive() + "}")
                .timestamp(Instant.now())
                .build());
    }

    private Long getCurrentActorUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0L;
        }

        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            email = jwt.getSubject();
        }

        if (email == null || email.isBlank()) {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return 0L;
        }

        return userRepository.findByEmail(email)
                .map(user -> parseToLongOrDefault(user.getId(), 0L))
                .orElse(0L);
    }

    private Long parseToLong(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(rawId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseToLongOrDefault(String rawId, Long defaultValue) {
        Long value = parseToLong(rawId);
        return value != null ? value : defaultValue;
    }

}
