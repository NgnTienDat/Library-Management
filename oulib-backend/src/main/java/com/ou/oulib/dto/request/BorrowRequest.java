package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu BorrowRequest")
public class BorrowRequest {
    @Schema(description = "Trường borrowerId", example = "id-001")
    String borrowerId;
    @NotNull
    @Min(0)
    @Schema(description = "Trường borrowDuration", example = "1")
    Integer borrowDuration;
    @Schema(description = "Trường barcodes", example = "[]")
    List<String> barcodes = new ArrayList<>();
}
