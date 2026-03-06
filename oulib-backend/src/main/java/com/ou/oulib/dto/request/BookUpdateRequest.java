package com.ou.oulib.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookUpdateRequest {
    String title;
    String publisher;
    Integer numberOfPages;
    String description;
    Integer totalCopies;
    String categoryId;
    List<AuthorRefRequest> authors;
}
