package com.ou.oulib.repository;

import com.ou.oulib.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {
    boolean existsByBarcodeIn(List<String> barcodes);
    Optional<BookCopy> findByBarcode(String barcode);
}
