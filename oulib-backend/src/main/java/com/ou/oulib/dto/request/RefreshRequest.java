package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "DTO yêu cầu RefreshRequest")
public class RefreshRequest {
    @Schema(description = "Trường token", example = "<JWT_TOKEN>")
    String token;
}