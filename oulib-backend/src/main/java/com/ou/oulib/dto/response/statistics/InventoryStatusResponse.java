package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi InventoryStatusResponse")
public class InventoryStatusResponse {
    @Schema(description = "Trường totalBooks", example = "1")
    long totalBooks;

    @Schema(description = "Trường totalBookCopies", example = "1")
    long totalBookCopies;

    @Schema(description = "Trường availableCopies", example = "1")
    long availableCopies;

    @Schema(description = "Trường borrowedCopies", example = "1")
    long borrowedCopies;

    @Schema(description = "Trường damagedCopies", example = "1")
    long damagedCopies;

    @Schema(description = "Trường lostCopies", example = "1")
    long lostCopies;

    @Schema(description = "Trường damagedOrLostCopies", example = "1")
    long damagedOrLostCopies;
}
