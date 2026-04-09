package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi CategoryResponse")
public class CategoryResponse {
    @Schema(description = "Trường id", example = "id-001")
    String id;
    @Schema(description = "Trường name", example = "sample")
    String name;
}
