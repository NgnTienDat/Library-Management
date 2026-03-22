package com.ou.oulib.repository;

import com.ou.oulib.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Book, String> {

    // Return ranked book ids instead of full Book entities to avoid ONLY_FULL_GROUP_BY issues.
    @Query("""
            SELECT b.id FROM Book b
            JOIN b.copies bc
            JOIN BorrowRecord br ON br.bookCopy = bc
            WHERE b.active = true
            GROUP BY b.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findPopularBookIds(Pageable pageable);

    // Return ranked book ids in a recent window (last 1 day).
    @Query("""
            SELECT b.id FROM Book b
            JOIN b.copies bc
            JOIN BorrowRecord br ON br.bookCopy = bc
            WHERE b.active = true
              AND br.borrowDate >= :since
            GROUP BY b.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findTrendingBookIds(@Param("since") LocalDate since, Pageable pageable);

    // Load full books with category/authors in one query after ranked-id lookup.
    @EntityGraph(attributePaths = {"category", "authors"})
    List<Book> findByIdIn(List<String> ids);

    @Query("""
            SELECT DISTINCT b.id FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            WHERE br.borrower.email = :email
            """)
    List<String> findBorrowedBookIdsByUserEmail(@Param("email") String email);

    @Query("""
            SELECT b.category.id FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            WHERE br.borrower.email = :email
              AND b.category IS NOT NULL
            GROUP BY b.category.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findTopCategoryIdsByUserEmail(@Param("email") String email, Pageable pageable);

    @Query("""
            SELECT a.id FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            JOIN b.authors a
            WHERE br.borrower.email = :email
            GROUP BY a.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findTopAuthorIdsByUserEmail(@Param("email") String email, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "authors"})
    @Query("""
            SELECT b FROM Book b
            WHERE b.active = true
              AND b.category.id = :categoryId
              AND b.id NOT IN :excludedBookIds
            """)
    List<Book> findByCategoryExcludingBookIds(
            @Param("categoryId") String categoryId,
            @Param("excludedBookIds") List<String> excludedBookIds,
            Pageable pageable);

    @EntityGraph(attributePaths = {"category", "authors"})
    @Query("""
            SELECT DISTINCT b FROM Book b
            JOIN b.authors a
            WHERE b.active = true
              AND a.id = :authorId
              AND b.id NOT IN :excludedBookIds
            """)
    List<Book> findByAuthorExcludingBookIds(
            @Param("authorId") String authorId,
            @Param("excludedBookIds") List<String> excludedBookIds,
            Pageable pageable);

    // Popular fallback while excluding already borrowed books; id-only to keep GROUP BY MySQL-safe.
    @Query("""
            SELECT b.id FROM Book b
            JOIN b.copies bc
            JOIN BorrowRecord br ON br.bookCopy = bc
            WHERE b.active = true
              AND b.id NOT IN :excludedBookIds
            GROUP BY b.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findPopularBookIdsExcludingBookIds(
            @Param("excludedBookIds") List<String> excludedBookIds,
            Pageable pageable);
}