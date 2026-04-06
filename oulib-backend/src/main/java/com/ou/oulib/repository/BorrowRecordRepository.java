package com.ou.oulib.repository;

import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.dto.response.statistics.DailyCountResponse;
import com.ou.oulib.dto.response.statistics.OverdueBookDetailResponse;
import com.ou.oulib.dto.response.statistics.OverdueUserInfoResponse;
import com.ou.oulib.dto.response.statistics.TopBorrowedBookResponse;
import com.ou.oulib.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, String> {

    boolean existsByBorrowerIdAndBookCopyIdAndStatus(String userId, String bookId, BorrowStatus status);

    boolean existsByBorrowerIdAndStatus(String userId, BorrowStatus status);

    long countByStatus(BorrowStatus status);

    Optional<BorrowRecord> findByBorrowerIdAndBookCopyIdAndStatus(String userId, String bookId, BorrowStatus status);

    Optional<BorrowRecord> findByBookCopyIdAndStatus(String bookCopyId, BorrowStatus status);

    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);

    Page<BorrowRecord> findByBorrowerId(String borrowerId, Pageable pageable);

    Page<BorrowRecord> findByBorrowerIdAndStatus(String borrowerId, BorrowStatus status, Pageable pageable);

    List<BorrowRecord> findByBorrowerIdOrderByBorrowDateDescCreatedAtDesc(String borrowerId);

    List<BorrowRecord> findByBorrowerIdAndStatusOrderByBorrowDateDescCreatedAtDesc(String borrowerId, BorrowStatus status);

    List<BorrowRecord> findByStatusAndReturnDateIsNullAndDueDate(BorrowStatus status, LocalDate dueDate);

    List<BorrowRecord> findByStatusAndReturnDateIsNullAndReminderSentFalseAndDueDate(BorrowStatus status, LocalDate dueDate);

    List<BorrowRecord> findByStatusAndReturnDateIsNullAndDueDateBefore(BorrowStatus status, LocalDate dueDate);

    List<BorrowRecord> findByStatusAndReturnDateIsNullAndReminderSentFalseAndDueDateBetween(BorrowStatus status, LocalDate start, LocalDate end);

    @Query(
            value = """
                    SELECT new com.ou.oulib.dto.response.statistics.OverdueUserInfoResponse(
                    br.borrower.id,
                    br.borrower.fullName,
                    br.borrower.email
                    )
                    FROM BorrowRecord br
                    WHERE br.status = :status
                      AND br.dueDate < :currentDate
                    GROUP BY br.borrower.id, br.borrower.fullName, br.borrower.email
                    ORDER BY br.borrower.fullName ASC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT br.borrower.id)
                    FROM BorrowRecord br
                    WHERE br.status = :status
                      AND br.dueDate < :currentDate
                    """
    )
    Page<OverdueUserInfoResponse> findOverdueUsers(
            @Param("status") BorrowStatus status,
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable
    );

    @Query("""
            SELECT new com.ou.oulib.dto.response.statistics.OverdueBookDetailResponse(
            br.borrower.id,
            br.borrower.fullName,
            br.borrower.email,
            bc.book.title,
            bc.barcode,
            br.dueDate
            )
            FROM BorrowRecord br
            JOIN br.bookCopy bc
            WHERE br.status = :status
              AND br.dueDate < :currentDate
              AND br.borrower.id IN :userIds
            ORDER BY br.borrower.fullName ASC, br.dueDate ASC
            """)
    List<OverdueBookDetailResponse> findOverdueDetailsByUserIds(
            @Param("status") BorrowStatus status,
            @Param("currentDate") LocalDate currentDate,
            @Param("userIds") List<String> userIds
    );

    @Query("""
            SELECT new com.ou.oulib.dto.response.statistics.DailyCountResponse(
            br.borrowDate,
            COUNT(br.id)
            )
            FROM BorrowRecord br
            WHERE br.borrowDate BETWEEN :fromDate AND :toDate
            GROUP BY br.borrowDate
            ORDER BY br.borrowDate ASC
            """)
    List<DailyCountResponse> countBorrowActivitiesByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT new com.ou.oulib.dto.response.statistics.DailyCountResponse(
            br.returnDate,
            COUNT(br.id)
            )
            FROM BorrowRecord br
            WHERE br.returnDate IS NOT NULL
              AND br.returnDate BETWEEN :fromDate AND :toDate
            GROUP BY br.returnDate
            ORDER BY br.returnDate ASC
            """)
    List<DailyCountResponse> countReturnActivitiesByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT new com.ou.oulib.dto.response.statistics.TopBorrowedBookResponse(
            b.id,
            b.title,
            COUNT(br.id)
            )
            FROM BorrowRecord br
            JOIN br.bookCopy bc
            JOIN bc.book b
            GROUP BY b.id, b.title
            ORDER BY COUNT(br.id) DESC
            """)
    List<TopBorrowedBookResponse> findTopBorrowedBooks(Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT br.borrower.id)
            FROM BorrowRecord br
            WHERE br.borrowDate BETWEEN :fromDate AND :toDate
            """)
    long countDistinctActiveUsersByBorrowDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
