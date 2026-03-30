package com.ou.oulib.repository;

import com.ou.oulib.entity.Book;
import com.ou.oulib.dto.response.statistics.CategoryDistributionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);
        boolean existsByCategoryId(String categoryId);

    // Top borrowed books of all time
    @Query("""
            SELECT b FROM Book b
            JOIN b.copies bc
            JOIN BorrowRecord br ON br.bookCopy = bc
            WHERE b.active = true
            GROUP BY b
            ORDER BY COUNT(br) DESC
            """)
    List<Book> findPopularBooks(Pageable pageable);

    // Top borrowed books since a given date
    @Query("""
            SELECT b FROM Book b
            JOIN b.copies bc
            JOIN BorrowRecord br ON br.bookCopy = bc
            WHERE b.active = true AND br.borrowDate >= :since
            GROUP BY b
            ORDER BY COUNT(br) DESC
            """)
    List<Book> findTrendingBooks(@Param("since") LocalDate since, Pageable pageable);

    // Books borrowed by a specific user (by email)
    @Query("""
            SELECT DISTINCT bc.book FROM BorrowRecord br
            JOIN br.bookCopy bc
            WHERE br.borrower.email = :email
            """)
    List<Book> findBooksBorrowedByUser(@Param("email") String email);

    // Most frequent category borrowed by the user
    @Query("""
            SELECT b.category.id FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            WHERE br.borrower.email = :email AND b.category IS NOT NULL
            GROUP BY b.category.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findTopCategoryIdsByUser(@Param("email") String email, Pageable pageable);

    // Most frequent author borrowed by the user
    @Query("""
            SELECT a.id FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            JOIN b.authors a
            WHERE br.borrower.email = :email
            GROUP BY a.id
            ORDER BY COUNT(br) DESC
            """)
    List<String> findTopAuthorIdsByUser(@Param("email") String email, Pageable pageable);

    // Recommend books by category, excluding already-borrowed books
    @Query("""
            SELECT b FROM Book b
            WHERE b.active = true
              AND b.category.id = :categoryId
              AND b NOT IN :excludedBooks
            """)
    List<Book> findByCategoryExcluding(
            @Param("categoryId") String categoryId,
            @Param("excludedBooks") List<Book> excludedBooks,
            Pageable pageable);

    // Recommend books by author, excluding already-borrowed books
    @Query("""
            SELECT DISTINCT b FROM Book b
            JOIN b.authors a
            WHERE b.active = true
              AND a.id = :authorId
              AND b NOT IN :excludedBooks
            """)
    List<Book> findByAuthorExcluding(
            @Param("authorId") String authorId,
            @Param("excludedBooks") List<Book> excludedBooks,
            Pageable pageable);

        @Query("""
                        SELECT new com.ou.oulib.dto.response.statistics.CategoryDistributionResponse(
                                c.id,
                                c.name,
                                COUNT(b.id)
                        )
                        FROM Book b
                        JOIN b.category c
                        GROUP BY c.id, c.name
                        ORDER BY c.name ASC
                        """)
        List<CategoryDistributionResponse> getCategoryDistribution();
}