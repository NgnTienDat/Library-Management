package com.ou.oulib.entity;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.ou.oulib.enums.UserRole;
import com.ou.oulib.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    String email;

//    @NotBlank(message = "Username is required")
//    @Size(min = 2, max = 100, message = "username must be between 2 and 100 characters")
//    @Column(nullable = false, unique = true, length = 100)
//    String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters and at most 16 characters")
    @Column(nullable = false)
    String password;

    @NotBlank(message = "full name is required")
    @Size(min = 1, max = 50, message = "Password must be at most 50 characters")
    String fullName;

    @ColumnDefault("true")
    boolean active;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    Instant createdAt;
    Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }


}
