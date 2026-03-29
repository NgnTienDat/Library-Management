package com.ou.oulib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    @NotBlank(message = "NOT_BLANK")
    String oldPassword;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String newPassword;
}