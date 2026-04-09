package com.ou.oulib.controller;

import com.ou.oulib.dto.response.statistics.ActiveUsersStatisticsResponse;
import com.ou.oulib.dto.response.statistics.BorrowingActivityResponse;
import com.ou.oulib.dto.response.statistics.InventorySummaryResponse;
import com.ou.oulib.dto.response.statistics.SystemTotalsResponse;
import com.ou.oulib.dto.response.statistics.TopBorrowedBookResponse;
import com.ou.oulib.service.StatisticsService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Statistics", description = "Nhóm API thống kê tồn kho, quá hạn, hoạt động mượn và chỉ số tổng quan hệ thống")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {

    StatisticsService statisticsService;

    @GetMapping("/statistics/inventory-summary")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
        @Operation(
            summary = "Lấy thống kê tồn kho",
            description = "Trả về số liệu tổng hợp sách, bản sao và phân bố theo danh mục"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thống kê tồn kho thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<InventorySummaryResponse>> getInventorySummary() {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getInventorySummary()));
    }

    @GetMapping("/statistics/overdue")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
        @Operation(
            summary = "Lấy danh sách quá hạn",
            description = "Trả về danh sách người dùng và sách đang quá hạn có phân trang"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách quá hạn thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> getOverdueRecords(
            @Parameter(description = "Số trang, bắt đầu từ 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getOverdueRecords(page, size)));
    }

    @GetMapping("/statistics/borrowing-activity")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
        @Operation(
            summary = "Lấy thống kê hoạt động mượn trả",
            description = "Trả về timeline số lượt mượn và trả theo khoảng ngày và đơn vị nhóm"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thống kê hoạt động thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<List<BorrowingActivityResponse>>> getBorrowingActivity(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Đơn vị nhóm dữ liệu: day, week hoặc month")
            @RequestParam(defaultValue = "day") String groupBy
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getBorrowingActivity(from, to, groupBy)));
    }

    @GetMapping("/statistics/top-books")
    @PreAuthorize("permitAll()")
        @Operation(
            summary = "Lấy top sách được mượn nhiều",
            description = "Trả về danh sách sách có số lượt mượn cao nhất theo giới hạn yêu cầu"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy top sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<List<TopBorrowedBookResponse>>> getTopBorrowedBooks(
            @Parameter(description = "Số lượng sách tối đa cần trả về")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getTopBorrowedBooks(limit)));
    }

    @GetMapping("/users/active-users")
    @PreAuthorize("hasRole('SYSADMIN')")
        @Operation(
            summary = "Lấy thống kê người dùng hoạt động",
            description = "Trả về số lượng người dùng hoạt động và người dùng mới trong khoảng thời gian chỉ định"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thống kê người dùng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<ActiveUsersStatisticsResponse>> getActiveUsersStatistics(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getActiveUsersStatistics(from, to)));
    }

    @GetMapping("/statistics/system-totals")
     @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
        @Operation(
            summary = "Lấy số liệu tổng quan hệ thống",
            description = "Trả về các chỉ số tổng hợp như tổng user, tổng sách, tổng bản ghi mượn và số lượng quá hạn"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy số liệu tổng quan thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<SystemTotalsResponse>> getSystemTotals() {
        return ResponseEntity.ok(ResponseUtils.ok(statisticsService.getSystemTotals()));
    }
}
