package com.ou.oulib.controller;

import com.ou.oulib.dto.request.BorrowRequest;
import com.ou.oulib.dto.request.BorrowRecordFilterRequest;
import com.ou.oulib.dto.request.ReturnRequest;
import com.ou.oulib.dto.response.BorrowRecordDetailResponse;
import com.ou.oulib.dto.response.BorrowRecordResponse;
import com.ou.oulib.service.BorrowService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrowing")
@Tag(name = "Borrowing", description = "Nhóm API quản lý nghiệp vụ mượn trả sách và tra cứu lịch sử mượn")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookBorrowController {

    BorrowService borrowService;

    @PostMapping()
    @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(
            summary = "Tạo phiếu mượn sách",
            description = "Thủ thư tạo bản ghi mượn cho người dùng theo danh sách barcode và thời hạn mượn"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo phiếu mượn thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu mượn không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền hoặc vi phạm điều kiện mượn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng hoặc sách"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Barcode bị trùng hoặc đã có bản ghi mượn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> borrowBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Thông tin mượn sách gồm người mượn, thời hạn và danh sách barcode",
                required = true
            )
            @RequestBody @Valid BorrowRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(borrowService.borrowBook(request, jwt)));
    }       

    @PostMapping("/return")
    @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(
            summary = "Trả sách",
            description = "Thủ thư thực hiện trả sách theo danh sách barcode của bản sao cần trả"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trả sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu trả sách không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sách hoặc bản ghi mượn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> returnBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Danh sách barcode bản sao sách cần trả",
                required = true
            )
            @RequestBody @Valid ReturnRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.returnBook(request, jwt)));
    }

    @GetMapping("/history")
        @Operation(
            summary = "Lấy lịch sử mượn của tôi",
            description = "Người dùng hiện tại xem lịch sử mượn/trả của chính mình, có thể lọc theo trạng thái"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy lịch sử mượn thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Giá trị trạng thái lọc không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> getMyBorrowingHistory(
            @Parameter(description = "Trạng thái lọc: borrowing, return/returned, overdue")
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getMyBorrowingHistory(status, jwt)));
    }

    @GetMapping("/{userId}/history")
    @PreAuthorize("hasAnyRole('LIBRARIAN','SYSADMIN')")
        @Operation(
            summary = "Lấy lịch sử mượn theo người dùng",
            description = "Thủ thư hoặc quản trị viên xem lịch sử mượn/trả của một người dùng cụ thể"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy lịch sử mượn thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Giá trị trạng thái lọc không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> getUserBorrowingHistory(
            @Parameter(description = "ID của người dùng cần xem lịch sử")
            @PathVariable String userId,
            @Parameter(description = "Trạng thái lọc: borrowing, return/returned, overdue")
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getUserBorrowingHistory(userId, status)));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('LIBRARIAN','SYSADMIN')")
        @Operation(
            summary = "Lấy danh sách bản ghi mượn",
            description = "Trả về danh sách bản ghi mượn có phân trang và hỗ trợ lọc theo trạng thái, người mượn"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách bản ghi mượn thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tham số lọc hoặc phân trang không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<PageResponse<BorrowRecordResponse>>> getBorrowRecords(
            @Parameter(description = "Trạng thái lọc: borrowing, return/returned, overdue")
            @RequestParam(required = false) String status,
            @Parameter(description = "ID người mượn cần lọc")
            @RequestParam(required = false) String borrowerId,
            @Parameter(description = "Số trang, bắt đầu từ 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang")
            @RequestParam(defaultValue = "10") int size
    ) {
        BorrowRecordFilterRequest request = BorrowRecordFilterRequest.builder()
                .status(status)
            .borrowerId(borrowerId)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getBorrowRecords(request)));
    }

    @GetMapping("/records/{recordId}")
    @PreAuthorize("hasAnyRole('LIBRARIAN','SYSADMIN')")
        @Operation(
            summary = "Lấy chi tiết bản ghi mượn",
            description = "Trả về thông tin chi tiết của một bản ghi mượn theo recordId"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết bản ghi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bản ghi mượn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<BorrowRecordDetailResponse>> getBorrowRecordDetail(
            @Parameter(description = "ID của bản ghi mượn cần xem")
            @PathVariable String recordId
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getBorrowRecordDetail(recordId)));
    }

    

}
