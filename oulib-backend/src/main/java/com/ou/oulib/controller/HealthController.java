package com.ou.oulib.controller;

import com.ou.oulib.service.BorrowService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Nhóm API kiểm tra tình trạng hoạt động của dịch vụ và kết nối RabbitMQ")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthController {

    BorrowService borrowService;

    @GetMapping
        @Operation(
            summary = "Kiểm tra sức khỏe dịch vụ",
            description = "Xác nhận API backend đang hoạt động bình thường"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dịch vụ hoạt động bình thường"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> healthCheck() {
        return ResponseEntity.ok(ResponseUtils.ok("Service is healthy"));
    }

    @GetMapping("/rabbitmq")
        @Operation(
            summary = "Kiểm tra kết nối RabbitMQ",
            description = "Gửi một thông điệp kiểm tra để xác nhận kết nối RabbitMQ khả dụng"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi thông điệp kiểm tra RabbitMQ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi kết nối RabbitMQ hoặc lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> rabbitmqCheck() {
        borrowService.rabbitmqConnectionCheck();
        return ResponseEntity.ok(ResponseUtils.ok("RabbitMQ connection test message sent"));
    }
}
