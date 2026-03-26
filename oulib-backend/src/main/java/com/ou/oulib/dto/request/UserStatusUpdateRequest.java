package com.ou.oulib.dto.request;

import com.ou.oulib.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatusUpdateRequest {

    @NotNull(message = "NOT_BLANK")
    UserStatus status;
}