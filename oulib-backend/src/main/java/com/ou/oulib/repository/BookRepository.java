package com.ou.oulib.repository;

import com.ou.oulib.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    boolean existsByIsbn(String isbn);
}