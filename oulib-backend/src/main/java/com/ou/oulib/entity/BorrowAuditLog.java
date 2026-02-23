package com.ou.oulib.entity;

import com.ou.oulib.enums.BorrowAction;
import com.ou.oulib.enums.UserRole;
import com.ou.oulib.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "borrow_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    String id;

    @ManyToOne
    @JoinColumn(name = "borrow_id", nullable = false)
    BorrowRecord borrowRecord;

    @ManyToOne
    @JoinColumn(name = "performed_by", nullable = false)
    User performedBy;

    @Enumerated(EnumType.STRING)
    BorrowAction action;

    String note;

    Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}