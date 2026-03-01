package com.ou.oulib.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
    String id;
    String title;
    String isbn;
    String publisher;
    Integer numberOfPages;
    String description;
    Integer totalCopies;
    Integer availableCopies;
    String thumbnailKey;
    String contentKey;
    String categoryName;
    List<String> authorNames;
}
