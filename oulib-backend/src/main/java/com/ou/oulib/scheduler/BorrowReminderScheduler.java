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

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BorrowReminderScheduler {

    BorrowRecordRepository borrowRecordRepository;
    BorrowReminderProducer borrowReminderProducer;

    /**
     * Runs once per day at midnight.
     * Queries borrow records that are due tomorrow and publishes reminder messages.
     */
//    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
//    public void sendBorrowReminders() {
//        log.info("Starting borrow reminder scheduler...");
//
//        LocalDate dueDate = LocalDate.now().plusDays(2);
//
//        List<BorrowRecord> recordsDueTomorrow = borrowRecordRepository
//                .findByStatusAndReturnDateIsNullAndReminderSentFalseAndDueDate(BorrowStatus.BORROWING, dueDate);
//
//        log.info("Found {} borrow records due tomorrow ({})", recordsDueTomorrow.size(), dueDate);
//
//        for (BorrowRecord record : recordsDueTomorrow) {
//            try {
//                borrowReminderProducer.publishBorrowReminder(record.getId());
//                record.setReminderSent(true);
//                log.debug("Published reminder for borrow record: {}", record.getId());
//            } catch (Exception e) {
//                log.error("Failed to publish reminder for borrow record: {}", record.getId(), e);
//            }
//        }
//
//        log.info("Borrow reminder scheduler completed.");
//    }


    @Scheduled(cron = "0 * * * * *") // mỗi phút
    @Transactional
    public void sendBorrowReminders() {
        log.info("Starting borrow reminder scheduler...");

        LocalDate today = LocalDate.now();

        List<BorrowRecord> recordsDueToday = borrowRecordRepository
                .findByStatusAndReturnDateIsNullAndReminderSentFalseAndDueDate(
                        BorrowStatus.BORROWING,
                        today
                );

        log.info("Found {} borrow records due today ({})", recordsDueToday.size(), today);

        for (BorrowRecord record : recordsDueToday) {
            try {
                borrowReminderProducer.publishBorrowReminder(record.getId());
                record.setReminderSent(true);
                log.debug("Published reminder for borrow record: {}", record.getId());
            } catch (Exception e) {
                log.error("Failed to publish reminder for borrow record: {}", record.getId(), e);
            }
        }

        log.info("Borrow reminder scheduler completed.");
    }

}


//
//package com.ou.oulib.scheduler;
//
//import com.ou.oulib.entity.BorrowRecord;
//import com.ou.oulib.enums.BorrowStatus;
//import com.ou.oulib.infras.producer.BorrowReminderProducer;
//import com.ou.oulib.repository.BorrowRecordRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class BorrowReminderScheduler {
//
//    BorrowRecordRepository borrowRecordRepository;
//    BorrowReminderProducer borrowReminderProducer;
//
//    /**
//     * Runs every minute.
//     * Queries borrow records that are due within 3 minutes and publishes reminder messages.
//     */
//    @Scheduled(cron = "0 * * * * *") // Run every minute
//    @Transactional
//    public void sendBorrowReminders() {
//        log.info("Starting borrow reminder scheduler...");
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime reminderTime = now.plusMinutes(2);
//
//        List<BorrowRecord> recordsDueSoon = borrowRecordRepository
//                .findByStatusAndReturnDateIsNullAndReminderSentFalseAndDueDateBetween(
//                        BorrowStatus.BORROWING, now, reminderTime);
//
//        log.info("Found {} borrow records due within 3 minutes ({})", recordsDueSoon.size(), reminderTime);
//
//        for (BorrowRecord record : recordsDueSoon) {
//            try {
//                borrowReminderProducer.publishBorrowReminder(record.getId());
//                record.setReminderSent(true);
//                log.debug("Published reminder for borrow record: {}", record.getId());
//            } catch (Exception e) {
//                log.error("Failed to publish reminder for borrow record: {}", record.getId(), e);
//            }
//        }
//
//        log.info("Borrow reminder scheduler completed.");
//    }
//}
