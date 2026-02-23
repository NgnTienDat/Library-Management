package com.ou.oulib.entity;

import com.ou.oulib.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
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
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;

    LocalDate borrowDate;
    LocalDate dueDate;
    LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    BorrowStatus status;

    boolean isLate;

    int renewedCount;

    @OneToMany(mappedBy = "borrowRecord")
    List<BorrowAuditLog> auditLogs = new ArrayList<>();

    @OneToMany(mappedBy = "borrowRecord")
    List<Notification> notifications = new ArrayList<>();

    Instant createdAt;
    Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}