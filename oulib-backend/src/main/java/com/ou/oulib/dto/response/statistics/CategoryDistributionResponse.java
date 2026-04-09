package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi CategoryDistributionResponse")
public class CategoryDistributionResponse {
    @Schema(description = "Trường categoryId", example = "id-001")
    String categoryId;
    @Schema(description = "Trường categoryName", example = "sample")
    String categoryName;
    @Schema(description = "Trường bookCount", example = "1")
    long bookCount;
}
