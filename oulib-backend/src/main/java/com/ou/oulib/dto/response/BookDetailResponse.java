package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi BookDetailResponse")
public class BookDetailResponse {
    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường active", example = "true")
    boolean active;
    @Schema(description = "Trường title", example = "Clean Code")
    String title;
    @Schema(description = "Trường isbn", example = "9786041234567")
    String isbn;
    @Schema(description = "Trường publisher", example = "Prentice Hall")
    String publisher;
    @Schema(description = "Trường numberOfPages", example = "1")
    Integer numberOfPages;
    @Schema(description = "Trường description", example = "Mo ta du lieu")
    String description;
    @Schema(description = "Trường copoies", example = "[]")
    List<String> copoies;
    @Schema(description = "Trường availableCopies", example = "1")
    Integer availableCopies;
    @Schema(description = "Trường thumbnailUrl", example = "https://example.com/resource")
    String thumbnailUrl;
    @Schema(description = "Trường categoryName", example = "sample")
    String categoryName;
    @Schema(description = "Trường authorNames", example = "[]")
    List<String> authorNames;
}