package com.ou.oulib.controller;

import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.request.BookFilterRequest;
import com.ou.oulib.dto.request.BookUpdateRequest;
import com.ou.oulib.dto.response.BookDetailResponse;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.service.BookService;
import com.ou.oulib.utils.ApiResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "Nhóm API quản lý sách: tạo mới, cập nhật, tra cứu, vô hiệu hóa và kích hoạt lại")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookService bookService;

    private List<String> parseAuthorIds(String authorIds) {
        if (authorIds == null || authorIds.isBlank()) {
            return List.of();
        }

        return Arrays.stream(authorIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isBlank())
                .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(
            summary = "Tạo sách mới",
            description = "Tạo mới đầu sách cùng danh sách bản sao và ảnh bìa tùy chọn"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu tạo sách không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục hoặc tác giả"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ISBN hoặc barcode đã tồn tại"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "413", description = "Tệp tải lên vượt quá dung lượng cho phép"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dữ liệu multipart gồm metadata sách và tệp thumbnail tùy chọn",
            required = true
        )
    public ResponseEntity<ApiResponse<?>> addNewBook(
            @Parameter(description = "Metadata mô tả thông tin sách cần tạo")
            @RequestPart("metadata") @Valid BookCreationRequest bookCreationRequest,
            @Parameter(description = "Ảnh bìa sách, có thể bỏ trống")
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(bookService.addNewBook(bookCreationRequest, thumbnail)));
    }

    @GetMapping
        @Operation(
            summary = "Lấy danh sách sách",
            description = "Trả về danh sách sách có lọc theo từ khóa, danh mục, tác giả và phân trang"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<?> getBooks(
            @Parameter(description = "Từ khóa tìm kiếm theo tiêu đề, danh mục hoặc tác giả")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "ID danh mục dùng để lọc")
            @RequestParam(required = false) String categoryId,
            @Parameter(description = "Danh sách authorId phân tách bằng dấu phẩy")
            @RequestParam(required = false) String authorIds,
            @Parameter(description = "Số trang, bắt đầu từ 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang")
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

    @GetMapping("/{id}")
        @Operation(
            summary = "Lấy chi tiết sách theo ID",
            description = "Trả về thông tin chi tiết của một đầu sách theo định danh"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy chi tiết sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sách"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
        public ResponseEntity<ApiResponse<BookDetailResponse>> getBookById(
            @Parameter(description = "ID của sách cần xem chi tiết")
            @PathVariable String id) {
        return ResponseEntity.ok(ResponseUtils.ok(bookService.getBookById(id)));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(
            summary = "Cập nhật thông tin sách",
            description = "Cập nhật một phần thông tin sách và ảnh bìa theo bookId"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu cập nhật không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sách, danh mục hoặc tác giả"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dữ liệu multipart gồm metadata cập nhật và ảnh thumbnail tùy chọn",
            required = true
        )
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "ID của sách cần cập nhật") @PathVariable String id,
            @Parameter(description = "Thông tin metadata cần cập nhật") @RequestPart("metadata") @Valid BookUpdateRequest request,
            @Parameter(description = "Ảnh bìa mới, có thể bỏ trống") @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(bookService.updateBook(id, request, thumbnail)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(
            summary = "Vô hiệu hóa sách",
            description = "Đánh dấu đầu sách là không hoạt động thay vì xóa cứng dữ liệu"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vô hiệu hóa sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sách"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
        public ResponseEntity<ApiResponse<String>> deleteBook(
            @Parameter(description = "ID của sách cần vô hiệu hóa")
            @PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ResponseUtils.ok("Book deactivated successfully"));
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(
            summary = "Kích hoạt lại sách",
            description = "Khôi phục trạng thái hoạt động cho đầu sách đã bị vô hiệu hóa"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kích hoạt lại sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sách"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
        public ResponseEntity<ApiResponse<String>> reactivateBook(
            @Parameter(description = "ID của sách cần kích hoạt lại")
            @PathVariable String id) {
        bookService.reactivateBook(id);
        return ResponseEntity.ok(ResponseUtils.ok("Book reactivated successfully"));
    }

}
