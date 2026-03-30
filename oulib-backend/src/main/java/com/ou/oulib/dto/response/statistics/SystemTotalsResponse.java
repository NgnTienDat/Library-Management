package com.ou.oulib.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemTotalsResponse {
    long totalUsers;
    long totalBooks;
    long totalCopies;
    long totalBorrowRecords;
    long totalCurrentlyBorrowed;
    long totalOverdue;
}