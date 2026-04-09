package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu UserUpdateRequest")
public class UserUpdateRequest {

    @Size(min = 1, max = 50, message = "Password must be at most 50 characters")
    @Schema(description = "Trường fullName", example = "Nguyen Van A")
    String fullName;

    @Size(min = 2, max = 100, message = "username must be between 2 and 100 characters")
    @Schema(description = "Trường username", example = "Nguyen Van A")
    String username;

    @Schema(description = "Trường avatar", example = "https://example.com/resource")
    String avatar;

}
