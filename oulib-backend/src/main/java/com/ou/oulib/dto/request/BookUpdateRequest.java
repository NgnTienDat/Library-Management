package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu BookUpdateRequest")
public class BookUpdateRequest {
    @Schema(description = "Trường title", example = "Clean Code")
    String title;
    @Schema(description = "Trường publisher", example = "Prentice Hall")
    String publisher;
    @Schema(description = "Trường numberOfPages", example = "1")
    Integer numberOfPages;
    @Schema(description = "Trường description", example = "Mo ta du lieu")
    String description;
    @Schema(description = "Trường totalCopies", example = "1")
    Integer totalCopies;
    @Schema(description = "Trường categoryId", example = "id-001")
    String categoryId;
    @Schema(description = "Trường authors", example = "[]")
    List<AuthorRefRequest> authors;
}
