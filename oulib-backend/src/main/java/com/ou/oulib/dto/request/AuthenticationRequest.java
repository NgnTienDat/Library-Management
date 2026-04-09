package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu AuthenticationRequest")
public class AuthenticationRequest {
    @NotBlank(message = "NOT_BLANK")
    @Email(message = "INVALID_EMAIL")
    @Schema(description = "Email", example = "user@example.com")
    String email;
    @NotBlank(message = "NOT_BLANK")
    @Schema(description = "Password", example = "Password@123")
    String password;
}