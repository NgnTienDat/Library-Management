package com.ou.oulib.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCreationRequest {
    String isbn;
    String title;
    String publisher;
    int numberOfPages;
    String description;
    int totalCopies;
    String categoryId;
    List<String> copyBarcodes = new ArrayList<>();
    List<AuthorRefRequest> authors = new ArrayList<>();
}
