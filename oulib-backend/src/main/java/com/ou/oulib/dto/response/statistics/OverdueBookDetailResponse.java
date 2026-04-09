package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi OverdueBookDetailResponse")
public class OverdueBookDetailResponse {
    @Schema(description = "Trường userId", example = "id-001")
    String userId;
    @Schema(description = "Trường userName", example = "Nguyen Van A")
    String userName;
    @Schema(description = "Trường email", example = "user@example.com")
    String email;
    @Schema(description = "Trường bookTitle", example = "Clean Code")
    String bookTitle;
    @Schema(description = "Trường barcode", example = "BC-0001")
    String barcode;
    @Schema(description = "Trường dueDate", example = "2026-04-08")
    LocalDate dueDate;
}
