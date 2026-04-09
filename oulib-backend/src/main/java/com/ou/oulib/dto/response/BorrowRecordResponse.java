package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "DTO phản hồi BorrowRecordResponse")
public class BorrowRecordResponse {
    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường borrowerId", example = "id-001")
    String borrowerId;
    @Schema(description = "Trường barcode", example = "BC-0001")
    String barcode;
    @Schema(description = "Trường borrowDate", example = "2026-04-08")
    LocalDate borrowDate;
    // LocalDate duDate;
    @Schema(description = "Trường dueDate", example = "2026-04-08T10:00:00")
    LocalDateTime dueDate;
    @Schema(description = "Trường returnDate", example = "2026-04-08")
    LocalDate returnDate;
    @Schema(description = "Trường status", example = "BORROWING")
    BorrowStatus status;
}
