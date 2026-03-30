package com.ou.oulib.controller;

import com.ou.oulib.dto.response.statistics.ActiveUsersStatisticsResponse;
import com.ou.oulib.dto.response.statistics.BorrowingActivityResponse;
import com.ou.oulib.dto.response.statistics.InventorySummaryResponse;
import com.ou.oulib.dto.response.statistics.SystemTotalsResponse;
import com.ou.oulib.dto.response.statistics.TopBorrowedBookResponse;
import com.ou.oulib.service.StatisticsService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {

    StatisticsService statisticsService;

    @GetMapping("/statistics/inventory-summary")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    public ResponseEntity<ApiResponse<InventorySummaryResponse>> getInventorySummary() {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getInventorySummary()));
    }

    @GetMapping("/statistics/overdue")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> getOverdueRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getOverdueRecords(page, size)));
    }

    @GetMapping("/statistics/borrowing-activity")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BorrowingActivityResponse>>> getBorrowingActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "day") String groupBy
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getBorrowingActivity(from, to, groupBy)));
    }

    @GetMapping("/statistics/top-books")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<TopBorrowedBookResponse>>> getTopBorrowedBooks(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getTopBorrowedBooks(limit)));
    }

    @GetMapping("/users/active-users")
    @PreAuthorize("hasRole('SYSADMIN')")
    public ResponseEntity<ApiResponse<ActiveUsersStatisticsResponse>> getActiveUsersStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getActiveUsersStatistics(from, to)));
    }

    @GetMapping("/statistics/system-totals")
    @PreAuthorize("hasRole('SYSADMIN')")
    public ResponseEntity<ApiResponse<SystemTotalsResponse>> getSystemTotals() {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getSystemTotals()));
    }
}
