package com.ou.oulib.service;

import com.ou.oulib.dto.request.AuthorRefRequest;
import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.request.BookFilterRequest;
import com.ou.oulib.dto.request.BookUpdateRequest;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BookCopy;
import com.ou.oulib.entity.Category;
import com.ou.oulib.enums.BookCopyStatus;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.mapper.BookMapper;
import com.ou.oulib.repository.AuthorRepository;
import com.ou.oulib.repository.BookCopyRepository;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.CategoryRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponse addNewBook(BookCreationRequest request,
            MultipartFile thumbnail) {

        if (request.getTotalCopies() <= 0) {
            throw new AppException(ErrorCode.INVALID_TOTAL_COPIES);
        }
        if (request.getCopyBarcodes().size() != request.getTotalCopies()) {
            throw new AppException(ErrorCode.COPY_IDS_COUNT_MISMATCH);
        }
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new AppException(ErrorCode.BOOK_ALREADY_EXISTS);
        }
        if (request.getCopyBarcodes().size() != request.getCopyBarcodes().stream().distinct().count()) {
            throw new AppException(ErrorCode.BARCODE_ALREADY_EXISTS);
        }
        if (bookCopyRepository.existsByBarcodeIn(request.getCopyBarcodes())) {
            throw new AppException(ErrorCode.BARCODE_ALREADY_EXISTS);
        }

        Book book = bookMapper.toBook(request);
        book.initializeCopies(request.getTotalCopies());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        book.setCategory(category);

        List<Author> authors = resolveAuthors(request.getAuthors());
        book.setAuthors(authors);

        List<BookCopy> copies = request.getCopyBarcodes().stream()
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
    public BookResponse getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return bookMapper.toBookResponse(book);
    }

    @Transactional
    @PreAuthorize("hasRole('SYSADMIN')")
    public BookResponse updateBook(String id, BookUpdateRequest request, MultipartFile newThumbnail) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

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

        return bookMapper.toBookResponse(book);
    }

    @Transactional
    @PreAuthorize("hasRole('SYSADMIN')")
    public void deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        book.setActive(false);
        bookRepository.save(book);
    }

}
