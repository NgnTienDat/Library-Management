package com.ou.oulib.dto.response;

import com.ou.oulib.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordResponse {
    String id;
    String bookId;
    String bookTitle;
    LocalDate borrowDate;
    LocalDate dueDate;
    BorrowStatus status;
}
