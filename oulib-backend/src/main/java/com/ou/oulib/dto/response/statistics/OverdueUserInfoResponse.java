package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi OverdueUserInfoResponse")
public class OverdueUserInfoResponse {
    @Schema(description = "Trường userId", example = "id-001")
    String userId;
    @Schema(description = "Trường userName", example = "Nguyen Van A")
    String userName;
    @Schema(description = "Trường email", example = "user@example.com")
    String email;
}
