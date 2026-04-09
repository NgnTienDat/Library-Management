package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu AuthorRefRequest")
public class AuthorRefRequest {

    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường name", example = "sample")
    String name;
}