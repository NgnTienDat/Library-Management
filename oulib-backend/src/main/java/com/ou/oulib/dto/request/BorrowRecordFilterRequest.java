package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu BorrowRecordFilterRequest")
public class BorrowRecordFilterRequest {
    @Schema(description = "Trường status", example = "ACTIVE")
    String status;
    @Schema(description = "Trường borrowerId", example = "id-001")
    String borrowerId;
    @Schema(description = "Trường page", example = "1")
    int page;
    @Schema(description = "Trường size", example = "1")
    int size;
}
