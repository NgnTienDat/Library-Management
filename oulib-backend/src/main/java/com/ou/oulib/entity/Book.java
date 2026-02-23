package com.ou.oulib.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    String id;

    @Column(nullable = false, unique = true, length = 20)
    String isbn;

    @Column(nullable = false)
    String title;

    String publisher;

    Integer numberOfPages;

    @Column(columnDefinition = "TEXT")
    String description;

    Integer totalCopies;

    Integer availableCopies;

    // ================= RELATIONSHIPS =================

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "created_by")
    User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    User updatedBy;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    List<Author> authors = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    List<BorrowRecord> borrowRecords = new ArrayList<>();

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