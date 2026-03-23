package com.ou.oulib.controller;

import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.request.BookFilterRequest;
import com.ou.oulib.dto.request.BookUpdateRequest;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.service.BookService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addNewBook(
            @RequestPart("metadata") @Valid BookCreationRequest bookCreationRequest,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(bookService.addNewBook(bookCreationRequest, thumbnail)));
    }

    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String authorIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<String> parsedAuthorIds = parseAuthorIds(authorIds);
        BookFilterRequest request = BookFilterRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .authorIds(parsedAuthorIds)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtils.ok(bookService.getBooks(request)));
    }

    private List<String> parseAuthorIds(String authorIds) {
        if (authorIds == null || authorIds.isBlank()) {
            return List.of();
        }

        return Arrays.stream(authorIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isBlank())
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(ResponseUtils.ok(bookService.getBookById(id)));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable String id,
            @RequestPart("metadata") @Valid BookUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(bookService.updateBook(id, request, thumbnail)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ResponseUtils.ok("Book deactivated successfully"));
    }

}
