package com.ou.oulib.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

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

    @Column(columnDefinition = "TEXT")
    String thumbnailUrl;

    String publisher;

    Integer numberOfPages;

    @Column(columnDefinition = "TEXT")
    String description;

    Integer totalCopies;

    Integer availableCopies;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BookCopy> copies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    List<Author> authors = new ArrayList<>();

    @ColumnDefault("true")
    boolean active;

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

    public void initializeCopies(int total) {
        this.availableCopies = total;
        this.totalCopies = total;
    }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
        copy.setBook(this);
    }
}