// package com.ou.oulib.service;

// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import lombok.experimental.NonFinal;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;

// @Service
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// @Slf4j
// public class EmailService {

//     JavaMailSender mailSender;

//     @NonFinal
//     @Value("${app.mail.username}")
//     String sendFrom;

//     private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

//     /**
//      * Sends a borrow reminder email to the user.
//      *
//      * @param toEmail   the recipient's email address
//      * @param bookTitle the title of the borrowed book
//      * @param dueDate   the due date of the book
//      * @throws MessagingException if email sending fails
//      */
//     public void sendBorrowReminderEmail(String toEmail, String bookTitle, LocalDate dueDate) throws MessagingException {
//         log.info("Sending borrow reminder email to: {} for book: {}", toEmail, bookTitle);

//         MimeMessage message = mailSender.createMimeMessage();
//         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//         helper.setFrom(sendFrom);
//         helper.setTo(toEmail);
//         helper.setSubject("Library Book Due Reminder");

//         String formattedDate = dueDate.format(DATE_FORMATTER);
//         String body = String.format(
//                 "You borrowed the book '%s'. It is due tomorrow (%s). Please return it to avoid overdue penalties.",
//                 bookTitle,
//                 formattedDate
//         );

//         helper.setText(body, false);

//         mailSender.send(message);
//         log.info("Successfully sent borrow reminder email to: {}", toEmail);
//     }
// }







package com.ou.oulib.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    JavaMailSender mailSender;

    @NonFinal
    @Value("${app.mail.username}")
    String sendFrom;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm");

    /**
     * Sends a borrow reminder email to the user.
     *
     * @param toEmail   the recipient's email address
     * @param bookTitle the title of the borrowed book
     * @param dueDate   the due date of the book
     * @throws MessagingException if email sending fails
     */
    public void sendBorrowReminderEmail(String toEmail, String bookTitle, LocalDate dueDate) throws MessagingException {
        log.info("Sending borrow reminder email to: {} for book: {}", toEmail, bookTitle);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(sendFrom);
        helper.setTo(toEmail);
        helper.setSubject("Library Book Due Reminder");

        String formattedDate = dueDate.format(DATE_TIME_FORMATTER);
        String body = String.format(
                "You borrowed the book '%s'. It is due soon at %s. Please return it to avoid overdue penalties.",
                bookTitle,
                formattedDate
        );

        helper.setText(body, false);

        mailSender.send(message);
        log.info("Successfully sent borrow reminder email to: {}", toEmail);
    }
}
