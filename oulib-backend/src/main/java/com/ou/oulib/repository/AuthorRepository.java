package com.ou.oulib.repository;

import com.ou.oulib.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
    Optional<Author> findByName(String name);
    List<Author> findAllByNameIn(List<String> names);
}