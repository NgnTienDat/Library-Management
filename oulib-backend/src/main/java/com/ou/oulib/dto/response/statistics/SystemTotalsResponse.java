package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi SystemTotalsResponse")
public class SystemTotalsResponse {
    @Schema(description = "Trường totalUsers", example = "1")
    long totalUsers;
    @Schema(description = "Trường totalBooks", example = "1")
    long totalBooks;
    @Schema(description = "Trường totalCopies", example = "1")
    long totalCopies;
    @Schema(description = "Trường totalBorrowRecords", example = "1")
    long totalBorrowRecords;
    @Schema(description = "Trường totalCurrentlyBorrowed", example = "1")
    long totalCurrentlyBorrowed;
    @Schema(description = "Trường totalOverdue", example = "1")
    long totalOverdue;
}