package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import com.ou.oulib.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu StaffCreationRequest")
public class StaffCreationRequest {

    @NotBlank(message = "NOT_BLANK")
    @Email(message = "INVALID_EMAIL")
    @Schema(description = "Trường email", example = "user@example.com")
    String email;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    @Schema(description = "Trường password", example = "Password@123")
    String password;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 1, max = 50, message = "INVALID_NAME")
    @Schema(description = "Trường fullName", example = "Nguyen Van A")
    String fullName;

    @NotNull(message = "NOT_BLANK")
    @Schema(description = "Trường role", example = "LIBRARIAN")
    UserRole role;
}