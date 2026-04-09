package com.ou.oulib.controller;

import com.ou.oulib.dto.request.CategoryCreationRequest;
import com.ou.oulib.dto.response.CategoryResponse;
import com.ou.oulib.service.CategoryService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Nhóm API quản lý danh mục sách")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

	CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('LIBRARIAN')")
	@Operation(
			summary = "Tạo danh mục",
			description = "Tạo mới danh mục sách và kiểm tra trùng tên theo quy tắc nghiệp vụ"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo danh mục thành công"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu danh mục không hợp lệ"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Danh mục đã tồn tại"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
	})
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Thông tin danh mục cần tạo",
					required = true
			)
			@RequestBody @Valid CategoryCreationRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseUtils.created(categoryService.createCategory(request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('LIBRARIAN')")
	@Operation(
			summary = "Xóa danh mục",
			description = "Xóa danh mục theo ID khi danh mục không còn được gán cho sách"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa danh mục thành công"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Danh mục đang được sử dụng"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
	})
	public ResponseEntity<ApiResponse<String>> deleteCategory(
			@Parameter(description = "ID danh mục cần xóa")
			@PathVariable String id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(ResponseUtils.ok("Category deleted successfully"));
	}

	@GetMapping
	@Operation(
			summary = "Lấy toàn bộ danh mục",
			description = "Trả về danh sách tất cả danh mục đang có trong hệ thống"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh mục thành công"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
	})
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
		return ResponseEntity.ok(ResponseUtils.ok(categoryService.getAllCategories()));
	}



}
