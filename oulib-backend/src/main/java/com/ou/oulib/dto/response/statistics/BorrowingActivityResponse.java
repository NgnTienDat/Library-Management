package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi BorrowingActivityResponse")
public class BorrowingActivityResponse {
    @Schema(description = "Trường time", example = "sample")
    String time;
    @Schema(description = "Trường borrowCount", example = "1")
    long borrowCount;
    @Schema(description = "Trường returnCount", example = "1")
    long returnCount;
}
