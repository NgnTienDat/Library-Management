package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi ActiveUsersStatisticsResponse")
public class ActiveUsersStatisticsResponse {
    @Schema(description = "Trường activeUsers", example = "1")
    long activeUsers;
    @Schema(description = "Trường newUsers", example = "1")
    long newUsers;
}
