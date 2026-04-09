package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi TopBorrowedBookResponse")
public class TopBorrowedBookResponse {
    @Schema(description = "Trường bookId", example = "id-001")
    String bookId;
    @Schema(description = "Trường title", example = "Clean Code")
    String title;
    @Schema(description = "Trường borrowCount", example = "1")
    long borrowCount;
}
