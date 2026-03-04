package com.ou.oulib.controller;

import com.ou.oulib.service.BorrowService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{bookId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookBorrowController {

    BorrowService borrowService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<?>> borrowBook(
            @PathVariable String bookId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(borrowService.borrowBook(bookId, jwt)));
    }       

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<?>> returnBook(
            @PathVariable String bookId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // TODO: Implement return logic
        return ResponseEntity.ok(ResponseUtils.ok("Return endpoint - not yet implemented"));
    }

    @GetMapping("/read")
    public ResponseEntity<ApiResponse<?>> readBook(
            @PathVariable String bookId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(borrowService.readBook(bookId, jwt)));
    }
}
