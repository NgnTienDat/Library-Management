package com.ou.oulib.repository;

import com.ou.oulib.entity.BorrowRecord;
import com.ou.oulib.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, String> {

    boolean existsByBorrowerIdAndBookCopyIdAndStatus(String userId, String bookId, BorrowStatus status);
    boolean existsByBorrowerIdAndStatus(String userId, BorrowStatus status);
    Optional<BorrowRecord> findByBorrowerIdAndBookCopyIdAndStatus(String userId, String bookId, BorrowStatus status);
}
