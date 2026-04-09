package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "DTO phản hồi BorrowRecordDetailResponse")
public class BorrowRecordDetailResponse {
    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường borrowerId", example = "id-001")
    String borrowerId;
    @Schema(description = "Trường borrowerFullName", example = "Nguyen Van A")
    String borrowerFullName;
    @Schema(description = "Trường borrowerEmail", example = "user@example.com")
    String borrowerEmail;
    @Schema(description = "Trường barcode", example = "BC-0001")
    String barcode;
    @Schema(description = "Trường borrowDate", example = "2026-04-08T10:00:00Z")
    Instant borrowDate;
    @Schema(description = "Trường dueDate", example = "2026-04-08")
    LocalDate dueDate;
    @Schema(description = "Trường status", example = "BORROWING")
    BorrowStatus status;
}