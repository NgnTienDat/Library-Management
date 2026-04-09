package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi UserResponse")
public class UserResponse {
    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường username", example = "Nguyen Van A")
    String username;
    @Schema(description = "Trường fullName", example = "Nguyen Van A")
    String fullName;
    @Schema(description = "Trường email", example = "user@example.com")
    String email;
    @Schema(description = "Trường role", example = "LIBRARIAN")
    String role;
    @Schema(description = "Trường avatar", example = "https://example.com/resource")
    String avatar;
    @Schema(description = "Trường status", example = "ACTIVE")
    String status;
    @Schema(description = "Trường active", example = "true")
    boolean active;
    @Schema(description = "Trường createdAt", example = "sample")
    String createdAt;
}
