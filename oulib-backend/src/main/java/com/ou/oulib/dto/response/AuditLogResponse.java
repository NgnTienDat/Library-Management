package com.ou.oulib.dto.response;

import com.ou.oulib.enums.AuditAction;
import com.ou.oulib.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private Long userId;
    private AuditAction action;
    private ResourceType resourceType;
    private Long resourceId;
    private String oldValue;
    private String newValue;
    private Instant createdAt;
}
