package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi TopBorrowedCategoryResponse")
public class TopBorrowedCategoryResponse {
    @Schema(description = "Trường categoryId", example = "id-001")
    String categoryId;

    @Schema(description = "Trường categoryName", example = "Công nghệ thông tin")
    String categoryName;

    @Schema(description = "Trường borrowCount", example = "1")
    long borrowCount;
}
