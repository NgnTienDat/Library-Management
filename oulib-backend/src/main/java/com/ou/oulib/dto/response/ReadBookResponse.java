package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi ReadBookResponse")
public class ReadBookResponse {
    @Schema(description = "Trường bookId", example = "id-001")
    String bookId;
    @Schema(description = "Trường bookTitle", example = "Clean Code")
    String bookTitle;
    @Schema(description = "Trường readUrl", example = "https://example.com/resource")
    String readUrl;
}
