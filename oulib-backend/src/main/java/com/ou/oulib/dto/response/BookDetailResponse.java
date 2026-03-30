package com.ou.oulib.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDetailResponse {
    String id;
    boolean active;
    String title;
    String isbn;
    String publisher;
    Integer numberOfPages;
    String description;
    List<String> copoies;
    Integer availableCopies;
    String thumbnailUrl;
    String categoryName;
    List<String> authorNames;
}