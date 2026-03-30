package com.ou.oulib.controller;

import com.ou.oulib.dto.request.CategoryCreationRequest;
import com.ou.oulib.dto.response.CategoryResponse;
import com.ou.oulib.service.CategoryService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

	CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('LIBRARIAN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
			@RequestBody @Valid CategoryCreationRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseUtils.created(categoryService.createCategory(request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('LIBRARIAN')")
	public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(ResponseUtils.ok("Category deleted successfully"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
		return ResponseEntity.ok(ResponseUtils.ok(categoryService.getAllCategories()));
	}



}
