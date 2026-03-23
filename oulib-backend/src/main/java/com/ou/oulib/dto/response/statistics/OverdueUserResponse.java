package com.ou.oulib.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OverdueUserResponse {
    String userId;
    String userName;
    String email;
    List<OverdueBookItemResponse> overdueBooks;
}
