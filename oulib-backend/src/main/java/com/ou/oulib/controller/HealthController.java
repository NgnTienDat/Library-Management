package com.ou.oulib.controller;

import com.ou.oulib.service.BorrowService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HealthController {

    BorrowService borrowService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> healthCheck() {
        return ResponseEntity.ok(ResponseUtils.ok("Service is healthy"));
    }

    @GetMapping("/rabbitmq")
    public ResponseEntity<ApiResponse<?>> rabbitmqCheck() {
        borrowService.rabbitmqConnectionCheck();
        return ResponseEntity.ok(ResponseUtils.ok("RabbitMQ connection test message sent"));
    }
}
