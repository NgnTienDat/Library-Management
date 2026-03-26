package com.ou.oulib.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotBlank(message = "NOT_BLANK")
    @Email(message = "INVALID_EMAIL")
    String email;
    @NotBlank(message = "NOT_BLANK")
    String password;
}