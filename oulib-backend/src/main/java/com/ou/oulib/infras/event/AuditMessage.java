package com.ou.oulib.infras.event;

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
public class AuditMessage {

    private Long userId;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String oldValue;
    private String newValue;
    private Instant timestamp;
}
