package com.ou.oulib.infras.consumer;

import com.ou.oulib.config.RabbitMQConfig;
import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.repository.BorrowRecordRepository;
import com.ou.oulib.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowReminderConsumer {

    BorrowRecordRepository borrowRecordRepository;
    EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.BORROW_REMINDER_QUEUE)
    @Transactional(readOnly = true)
    public void handleBorrowReminder(String borrowRecordId) {
        log.info("Received borrow reminder message for record: {}", borrowRecordId);

        try {
            // Load the borrow record from database
            BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                    .orElseThrow(() -> new RuntimeException("BorrowRecord not found: " + borrowRecordId));

            // Extract required information
            String borrowerEmail = record.getBorrower().getEmail();
            String bookTitle = record.getBookCopy().getBook().getTitle();
            var dueDate = record.getDueDate();

            log.info("Sending reminder to {} for book '{}' due on {}", borrowerEmail, bookTitle, dueDate);

            // Send reminder email
            emailService.sendBorrowReminderEmail(borrowerEmail, bookTitle, dueDate);

            log.info("Successfully processed borrow reminder for record: {}", borrowRecordId);

        } catch (Exception e) {
            log.error("Failed to process borrow reminder for record: {}. Error: {}", borrowRecordId, e.getMessage(), e);
            // Re-throw exception so RabbitMQ can retry
            throw new RuntimeException("Failed to send borrow reminder email for record: " + borrowRecordId, e);
        }
    }
}
