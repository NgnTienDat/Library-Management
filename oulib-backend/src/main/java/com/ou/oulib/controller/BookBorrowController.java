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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookBorrowController {

    BorrowService borrowService;

    @PostMapping()
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> borrowBook(
            @RequestBody @Valid BorrowRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(borrowService.borrowBook(request, jwt)));
    }       

    @PostMapping("/return")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> returnBook(
            @RequestBody @Valid ReturnRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.returnBook(request, jwt)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<?>> getMyBorrowingHistory(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getMyBorrowingHistory(status, jwt)));
    }

    @GetMapping("/{userId}/history")
    @PreAuthorize("hasAnyRole('LIBRARIAN','SYSADMIN')")
    public ResponseEntity<ApiResponse<?>> getUserBorrowingHistory(
            @PathVariable String userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getUserBorrowingHistory(userId, status)));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('LIBRARIAN','SYSADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BorrowRecordResponse>>> getBorrowRecords(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String borrowerId,
            @RequestParam(defaultValue = "0") int page,
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
    public ResponseEntity<ApiResponse<BorrowRecordDetailResponse>> getBorrowRecordDetail(
            @PathVariable String recordId
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.getBorrowRecordDetail(recordId)));
    }

    

}
