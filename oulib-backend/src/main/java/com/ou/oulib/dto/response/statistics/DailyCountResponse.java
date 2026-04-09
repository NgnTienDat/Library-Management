package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi DailyCountResponse")
public class DailyCountResponse {
    @Schema(description = "Trường date", example = "2026-04-08")
    LocalDate date;
    @Schema(description = "Trường count", example = "1")
    long count;
}
