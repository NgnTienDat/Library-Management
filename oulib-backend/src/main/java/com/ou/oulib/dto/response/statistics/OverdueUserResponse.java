package com.ou.oulib.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi OverdueUserResponse")
public class OverdueUserResponse {
    @Schema(description = "Trường userId", example = "id-001")
    String userId;
    @Schema(description = "Trường userName", example = "Nguyen Van A")
    String userName;
    @Schema(description = "Trường email", example = "user@example.com")
    String email;
    @Schema(description = "Trường overdueBooks", example = "[]")
    List<OverdueBookItemResponse> overdueBooks;
}
