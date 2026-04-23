package com.ou.oulib.controller;

import com.ou.oulib.dto.response.AuditLogResponse;
import com.ou.oulib.service.AuditLogService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Nhóm API tra cứu nhật ký audit")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogController {

    AuditLogService auditLogService;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách audit log",
            description = "Chỉ SYSADMIN được phép xem danh sách audit log có phân trang"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách audit log thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PreAuthorize("hasRole('SYSADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @Parameter(description = "Số trang, bắt đầu từ 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(auditLogService.getAuditLogs(page, size)));
    }
}
