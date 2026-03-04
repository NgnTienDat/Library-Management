package com.ou.oulib.service;

import com.ou.oulib.dto.response.BorrowRecordResponse;
import com.ou.oulib.dto.response.ReadBookResponse;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.entity.User;
import com.ou.oulib.enums.BorrowStatus;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.BorrowRecordRepository;
import com.ou.oulib.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowService {

    BorrowRecordRepository borrowRecordRepository;
    BookRepository bookRepository;
    UserRepository userRepository;
    S3Service s3Service;

    @Transactional
    public BorrowRecordResponse borrowBook(String bookId, Jwt jwt) {
        User user = getAuthenticatedUser(jwt);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // Check borrowing quota
        if (user.getBorrowQuota() <= 0) {
            throw new AppException(ErrorCode.BORROW_QUOTA_EXCEEDED);
        }

        // Check available copies
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new AppException(ErrorCode.NO_AVAILABLE_COPIES);
        }

        // Check duplicate active borrow
        if (borrowRecordRepository.existsByUserIdAndBookIdAndStatus(
                user.getId(), book.getId(), BorrowStatus.BORROWING)) {
            throw new AppException(ErrorCode.ALREADY_BORROWING);
        }

        // Create borrow record
        LocalDate now = LocalDate.now();
        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(now)
                .dueDate(now.plusDays(15))
                .status(BorrowStatus.BORROWING)
                .isLate(false)
                .renewedCount(0)
                .build();

        borrowRecordRepository.save(record);

        // Decrease available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Decrease user borrow quota
        user.setBorrowQuota(user.getBorrowQuota() - 1);
        userRepository.save(user);

        log.info("User {} borrowed book {} successfully", user.getEmail(), book.getTitle());

        return toBorrowRecordResponse(record);
    }

    @Transactional(readOnly = true)
    public ReadBookResponse readBook(String bookId, Jwt jwt) {
        User user = getAuthenticatedUser(jwt);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // Check active borrow record exists
        BorrowRecord record = borrowRecordRepository
                .findByUserIdAndBookIdAndStatus(user.getId(), book.getId(), BorrowStatus.BORROWING)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

        // Check due date not exceeded
        if (LocalDate.now().isAfter(record.getDueDate())) {
            throw new AppException(ErrorCode.BORROW_EXPIRED);
        }

        // Validate book has digital content
        if (book.getContentKey() == null || book.getContentKey().isBlank()) {
            throw new AppException(ErrorCode.BOOK_CONTENT_NOT_AVAILABLE);
        }

        String url = s3Service.generatePreSignedUrl(book.getContentKey());

        return ReadBookResponse.builder()
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .readUrl(url)
                .build();
    }

    private User getAuthenticatedUser(Jwt jwt) {
        String email = jwt.getSubject();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private BorrowRecordResponse toBorrowRecordResponse(BorrowRecord record) {
        return BorrowRecordResponse.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .status(record.getStatus())
                .build();
    }
}
