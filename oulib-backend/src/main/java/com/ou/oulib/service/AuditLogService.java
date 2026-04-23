package com.ou.oulib.service;

import com.ou.oulib.dto.response.AuditLogResponse;
import com.ou.oulib.entity.AuditLog;
import com.ou.oulib.repository.AuditLogRepository;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.PageResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogService {

    AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SYSADMIN')")
    public PageResponse<AuditLogResponse> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        Page<AuditLog> auditLogs = auditLogRepository.findAll(pageable);
        return PageResponseUtils.build(auditLogs, this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .action(auditLog.getAction())
                .resourceType(auditLog.getResourceType())
                .resourceId(auditLog.getResourceId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
