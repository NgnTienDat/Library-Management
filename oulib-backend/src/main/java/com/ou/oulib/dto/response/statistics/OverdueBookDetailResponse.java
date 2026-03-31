package com.ou.oulib.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OverdueBookDetailResponse {
    String userId;
    String userName;
    String email;
    String bookTitle;
    String barcode;
    LocalDate dueDate;
}
