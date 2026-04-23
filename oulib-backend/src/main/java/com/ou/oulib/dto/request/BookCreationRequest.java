package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu BookCreationRequest")
public class BookCreationRequest {
    @Schema(description = "Trường isbn", example = "9786041234567")
    String isbn;
    @Schema(description = "Trường title", example = "Clean Code")
    String title;
    @Schema(description = "Trường publisher", example = "Prentice Hall")
    String publisher;
    @Schema(description = "Trường numberOfPages", example = "1")
    int numberOfPages;
    @Schema(description = "Trường description", example = "Mo ta du lieu")
    String description;
    @Schema(description = "Trường categoryId", example = "id-001")
    String categoryId;
    @Builder.Default
    @Schema(description = "Trường copyBarcodes", example = "[]")
    List<String> copyBarcodes = new ArrayList<>();
    @Builder.Default
    @Schema(description = "Trường authors", example = "[]")
    List<AuthorRefRequest> authors = new ArrayList<>();
}
