package com.ou.oulib.service;

import com.ou.oulib.dto.request.CategoryCreationRequest;
import com.ou.oulib.dto.response.CategoryResponse;
import com.ou.oulib.entity.Category;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;
    BookRepository bookRepository;

    @PreAuthorize("hasRole('LIBRARIAN')")
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        String normalizedName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = Category.builder()
                .name(normalizedName)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (bookRepository.existsByCategoryId(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
