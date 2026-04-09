package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "DTO phản hồi IntrospectResponse")
public class IntrospectResponse {
    @Schema(description = "Trường valid", example = "true")
    boolean valid;
}