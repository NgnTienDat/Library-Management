package com.ou.oulib.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDistributionResponse {
    String categoryId;
    String categoryName;
    long bookCount;
}
