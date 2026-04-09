package com.ou.oulib.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import com.ou.oulib.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO yêu cầu UserStatusUpdateRequest")
public class UserStatusUpdateRequest {

    @NotNull(message = "NOT_BLANK")
    @Schema(description = "Trường status", example = "ACTIVE")
    UserStatus status;
}