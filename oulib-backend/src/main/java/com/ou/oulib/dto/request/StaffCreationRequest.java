package com.ou.oulib.dto.request;

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
public class StaffCreationRequest {

    @NotBlank(message = "NOT_BLANK")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 1, max = 50, message = "INVALID_NAME")
    String fullName;

    @NotNull(message = "NOT_BLANK")
    UserRole role;
}