package com.ou.oulib.dto.response;

import com.ou.oulib.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordResponse {
    String id;
    String barcode;
    LocalDate borrowDate;
    // LocalDate duDate;
    LocalDateTime dueDate;
    LocalDate returnDate;
    BorrowStatus status;
}
