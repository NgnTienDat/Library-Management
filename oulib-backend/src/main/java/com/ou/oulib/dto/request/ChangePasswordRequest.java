package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu ChangePasswordRequest")
public class ChangePasswordRequest {

    @NotBlank(message = "NOT_BLANK")
    @Schema(description = "Trường oldPassword", example = "Password@123")
    String oldPassword;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    @Schema(description = "Trường newPassword", example = "Password@123")
    String newPassword;
}