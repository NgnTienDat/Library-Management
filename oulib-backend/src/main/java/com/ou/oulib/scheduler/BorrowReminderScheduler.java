// package com.ou.oulib.scheduler;

// import com.ou.oulib.entity.BorrowRecord;
// import com.ou.oulib.enums.BorrowStatus;
// import com.ou.oulib.infras.producer.BorrowReminderProducer;
// import com.ou.oulib.repository.BorrowRecordRepository;
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// @Slf4j
// public class BorrowReminderScheduler {

//     BorrowRecordRepository borrowRecordRepository;
//     BorrowReminderProducer borrowReminderProducer;

//     /**
//      * Runs once per day at midnight.
//      * Queries borrow records that are due tomorrow and publishes reminder messages.
//      */
//     @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
//     @Transactional(readOnly = true)
//     public void sendBorrowReminders() {
//         log.info("Starting borrow reminder scheduler...");

//         LocalDate tomorrow = LocalDate.now().plusDays(1);

//         List<BorrowRecord> recordsDueTomorrow = borrowRecordRepository
//                 .findByStatusAndReturnDateIsNullAndDueDate(BorrowStatus.BORROWING, tomorrow);

//         log.info("Found {} borrow records due tomorrow ({})", recordsDueTomorrow.size(), tomorrow);

//         for (BorrowRecord record : recordsDueTomorrow) {
//             try {
//                 borrowReminderProducer.publishBorrowReminder(record.getId());
//                 log.debug("Published reminder for borrow record: {}", record.getId());
//             } catch (Exception e) {
//                 log.error("Failed to publish reminder for borrow record: {}", record.getId(), e);
//             }
//         }

//         log.info("Borrow reminder scheduler completed.");
//     }
// }



package com.ou.oulib.scheduler;

import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.enums.BorrowStatus;
import com.ou.oulib.infras.producer.BorrowReminderProducer;
import com.ou.oulib.repository.BorrowRecordRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowReminderScheduler {

    BorrowRecordRepository borrowRecordRepository;
    BorrowReminderProducer borrowReminderProducer;

    /**
     * Runs every minute.
     * Queries borrow records that are due within 3 minutes and publishes reminder messages.
     */
    @Scheduled(cron = "0 * * * * *") // Run every minute
    @Transactional(readOnly = true)
    public void sendBorrowReminders() {
        log.info("Starting borrow reminder scheduler...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(3);

        List<BorrowRecord> recordsDueSoon = borrowRecordRepository
                .findByStatusAndReturnDateIsNullAndDueDateBetween(BorrowStatus.BORROWING, now, reminderTime);

        log.info("Found {} borrow records due within 3 minutes ({})", recordsDueSoon.size(), reminderTime);

        for (BorrowRecord record : recordsDueSoon) {
            try {
                borrowReminderProducer.publishBorrowReminder(record.getId());
                log.debug("Published reminder for borrow record: {}", record.getId());
            } catch (Exception e) {
                log.error("Failed to publish reminder for borrow record: {}", record.getId(), e);
            }
        }

        log.info("Borrow reminder scheduler completed.");
    }
}
