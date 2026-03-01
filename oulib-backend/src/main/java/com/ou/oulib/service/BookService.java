package com.ou.oulib.service;


import com.ou.oulib.dto.request.AuthorRefRequest;
import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.Category;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.mapper.BookMapper;
import com.ou.oulib.repository.AuthorRepository;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.CategoryRepository;
import com.ou.oulib.utils.Helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookService {

    BookRepository bookRepository;
    CategoryRepository categoryRepository;
    AuthorRepository authorRepository;
    BookMapper bookMapper;
    CloudinaryUploadService cloudinaryUploadService;
    S3Service s3Service;


    //    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public BookResponse addNewBook(BookCreationRequest request,
                                   MultipartFile file,
                                   MultipartFile thumbnail) throws IOException {

        if (request.getTotalCopies() <= 0) {
            throw new AppException(ErrorCode.INVALID_TOTAL_COPIES);
        }

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new AppException(ErrorCode.BOOK_ALREADY_EXISTS);
        }

        Book book = bookMapper.toBook(request);
        book.initializeCopies(request.getTotalCopies());

        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        book.setCategory(category);

        List<Author> authors = resolveAuthors(request.getAuthors());
        book.setAuthors(authors);

        book = bookRepository.save(book);

        String contentKey = null;
        String thumbnailKey = null;

        try {
            // Upload PDF
            if (file != null && !file.isEmpty()) {
                Helper.validatePdf(file);
                contentKey = s3Service.uploadFile(file, book.getId());
                book.setContentKey(contentKey);
            }
            // Upload thumbnail
            if (thumbnail != null && !thumbnail.isEmpty()) {
                Helper.validateImage(thumbnail);
                thumbnailKey = cloudinaryUploadService.uploadThumbnail(thumbnail);
                book.setThumbnailKey(thumbnailKey);
            }
            // It's not necessary to explicitly save the book (file, thumbnail)
            // again because it's still in the persistence context.
            return bookMapper.toBookResponse(book);

        } catch (Exception ex) {

            // If upload fails → delete the newly created DB record
            bookRepository.delete(book);
            cleanupUploadedFiles(contentKey, thumbnailKey);
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
                                .build()
                );
                result.add(newAuthor);
            }
        }
        return result;
    }

    private void cleanupUploadedFiles(String contentKey, String thumbnailKey) {

        if (contentKey != null) {
            try {
                s3Service.deleteFile(contentKey);
            } catch (Exception ex) {
                log.error("Failed to cleanup S3 file {}", contentKey, ex);
            }
        }

        if (thumbnailKey != null) {
            try {
                cloudinaryUploadService.delete(thumbnailKey);
            } catch (Exception ex) {
                log.error("Failed to cleanup thumbnail {}", thumbnailKey, ex);
            }
        }
    }


}
