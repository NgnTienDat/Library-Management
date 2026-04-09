package com.ou.oulib.entity;

import com.ou.oulib.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String barcode;

    @Enumerated(EnumType.STRING)
    BookCopyStatus status;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;
}