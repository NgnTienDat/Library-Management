package com.ou.oulib.dto.response;

import com.ou.oulib.enums.BorrowStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordDetailResponse {
    String id;
    String borrowerId;
    String borrowerFullName;
    String borrowerEmail;
    String barcode;
    Instant borrowDate;
    LocalDate dueDate;
    BorrowStatus status;
}