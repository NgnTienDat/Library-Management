package com.ou.oulib.controller;

import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.service.RecommendationService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@Tag(name = "Recommendations", description = "Nhóm API gợi ý sách phổ biến, xu hướng và cá nhân hóa")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController {

    RecommendationService recommendationService;

    @GetMapping("/popular")
        @Operation(
            summary = "Lấy sách gợi ý phổ biến",
            description = "Trả về danh sách sách phổ biến dựa trên tần suất mượn toàn hệ thống"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy gợi ý phổ biến thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getPopularBooks() {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getPopularBooks()));
    }

    @GetMapping("/trending")
        @Operation(
            summary = "Lấy sách gợi ý xu hướng",
            description = "Trả về danh sách sách có xu hướng mượn cao trong khoảng thời gian gần đây"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy gợi ý xu hướng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getTrendingBooks() {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getTrendingBooks()));
    }

    @GetMapping("/personalized")
        @Operation(
            summary = "Lấy sách gợi ý cá nhân hóa",
            description = "Trả về danh sách gợi ý theo lịch sử mượn của người dùng hiện tại, có fallback sang danh sách phổ biến"
        )
        @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy gợi ý cá nhân hóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<List<BookResponse>>> getPersonalizedRecommendations(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getPersonalizedRecommendations(jwt)));
    }
}
