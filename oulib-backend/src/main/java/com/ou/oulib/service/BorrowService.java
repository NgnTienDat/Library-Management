package com.ou.oulib.service;

import com.ou.oulib.dto.request.BorrowRequest;
import com.ou.oulib.dto.response.BorrowRecordResponse;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BookCopy;
import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.entity.User;
import com.ou.oulib.enums.BookCopyStatus;
import com.ou.oulib.enums.BorrowStatus;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.enums.UserStatus;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.infras.event.ActionMessage;
import com.ou.oulib.infras.producer.RabbitMQPublisher;
import com.ou.oulib.mapper.BorrowRecordMapper;
import com.ou.oulib.repository.BookCopyRepository;
import com.ou.oulib.repository.BorrowRecordRepository;
import com.ou.oulib.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowService {

    BorrowRecordRepository borrowRecordRepository;
    UserRepository userRepository;
    BookCopyRepository bookCopyRepository;
    BorrowRecordMapper borrowRecordMapper;
    RabbitMQPublisher rabbitMQPublisher;


    public void rabbitmqConnectionCheck() {
        ActionMessage actionMessage = ActionMessage.builder()
                .postId("test-post-id")
                .actionType("TEST_ACTION")
                .userId("test-user-id")
                .createdAt(Instant.now())
                .build();
        rabbitMQPublisher.publishActionMessage(actionMessage);

    }

    @Transactional
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<BorrowRecordResponse> borrowBook(BorrowRequest request, Jwt jwt) {
        List<String> barcodes = request.getBarcodes();

        if (barcodes.size() != barcodes.stream().distinct().count())
            throw new AppException(ErrorCode.BARCODE_ALREADY_EXISTS);

        User borrower = userRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User librarian = getAuthenticatedUser(jwt);

        if (borrower.getStatus().equals(UserStatus.SUSPENDED))
            throw new AppException(ErrorCode.USER_INACTIVE);

        if (borrowRecordRepository.existsByBorrowerIdAndStatus(borrower.getId(), BorrowStatus.OVERDUE))
            throw new AppException(ErrorCode.USER_HAS_OVERDUE_BOOK);

        if (barcodes.size() > borrower.getBorrowQuota())
            throw new AppException(ErrorCode.BORROW_QUOTA_EXCEEDED);

        List<BorrowRecordResponse> responses = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (String barcode : barcodes) {
            BookCopy bookCopy = bookCopyRepository.findByBarcode(barcode)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE)
                throw new AppException(ErrorCode.NO_AVAILABLE_COPIES);

            if (borrowRecordRepository.existsByBorrowerIdAndBookCopyIdAndStatus(
                    borrower.getId(), bookCopy.getId(), BorrowStatus.BORROWING))
                throw new AppException(ErrorCode.ALREADY_BORROWING);

            Book book = bookCopy.getBook();
            BorrowRecord record = BorrowRecord.builder()
                    .borrower(borrower)
                    .librarian(librarian)
                    .bookCopy(bookCopy)
                    .borrowDate(now)
                    .dueDate(now.plusDays(14))
                    .status(BorrowStatus.BORROWING)
                    .build();

            borrowRecordRepository.save(record);

            bookCopy.setStatus(BookCopyStatus.BORROWED);
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            borrower.setBorrowQuota(borrower.getBorrowQuota() - 1);

            responses.add(borrowRecordMapper.toBorrowRecordResponse(record));
        }

        return responses;
    }


    private User getAuthenticatedUser(Jwt jwt) {
        String email = jwt.getSubject();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
