package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi InventorySummaryResponse")
public class InventorySummaryResponse {
    @Schema(description = "Trường totalBooks", example = "1")
    long totalBooks;
    @Schema(description = "Trường totalBookCopies", example = "1")
    long totalBookCopies;
    @Schema(description = "Trường availableCopies", example = "1")
    long availableCopies;
    @Schema(description = "Trường borrowedCopies", example = "1")
    long borrowedCopies;
    @Schema(description = "Trường availabilityRate", example = "1")
    double availabilityRate;
    @Schema(description = "Trường categoryDistribution", example = "[]")
    List<CategoryDistributionResponse> categoryDistribution;
}
