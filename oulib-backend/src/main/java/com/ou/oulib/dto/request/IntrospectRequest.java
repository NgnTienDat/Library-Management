package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "DTO yêu cầu IntrospectRequest")
public class IntrospectRequest {
    @NotBlank
    @Schema(description = "Trường token", example = "<JWT_TOKEN>")
    String token;
}
