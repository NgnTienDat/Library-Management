package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi AuthenticationResponse")
public class AuthenticationResponse {
    @Schema(description = "Trường authenticated", example = "true")
    boolean authenticated;
    @Schema(description = "Trường user", example = "sample")
    UserResponse user;
    @Schema(description = "Trường token", example = "<JWT_TOKEN>")
    String token;
}
