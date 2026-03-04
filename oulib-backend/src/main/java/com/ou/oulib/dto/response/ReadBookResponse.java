package com.ou.oulib.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadBookResponse {
    String bookId;
    String bookTitle;
    String readUrl;
}
