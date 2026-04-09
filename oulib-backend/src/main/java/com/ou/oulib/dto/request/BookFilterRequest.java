package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu BookFilterRequest")
public class BookFilterRequest {
    @Schema(description = "Trường keyword", example = "sample")
    String keyword;
    @Schema(description = "Trường categoryId", example = "id-001")
    String categoryId;

    @Builder.Default
    @Schema(description = "Trường authorIds", example = "[]")
    List<String> authorIds = new ArrayList<>();

    @Schema(description = "Trường page", example = "1")
    int page;
    @Schema(description = "Trường size", example = "1")
    int size;
}
