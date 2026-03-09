package com.ou.oulib.entity;

import com.ou.oulib.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    User borrower;

    @ManyToOne
    @JoinColumn(name = "librarian_id", nullable = false)
    User librarian;

    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    BookCopy bookCopy;

    @Enumerated(EnumType.STRING)
    BorrowStatus status;

    LocalDate borrowDate;
    // LocalDate dueDate;
    LocalDateTime dueDate;
    LocalDate returnDate;
    Instant createdAt;
    Instant updatedAt;

    @Column(nullable = false)
    @Builder.Default
    boolean reminderSent = false;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}