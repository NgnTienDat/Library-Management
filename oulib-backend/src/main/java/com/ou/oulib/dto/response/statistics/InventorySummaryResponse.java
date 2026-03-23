package com.ou.oulib.dto.response.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventorySummaryResponse {
    long totalBooks;
    long totalBookCopies;
    long availableCopies;
    long borrowedCopies;
    double availabilityRate;
    List<CategoryDistributionResponse> categoryDistribution;
}
