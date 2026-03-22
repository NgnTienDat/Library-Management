package com.ou.oulib.controller;

import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.service.RecommendationService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController {

    RecommendationService recommendationService;

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getPopularBooks() {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getPopularBooks()));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getTrendingBooks() {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getTrendingBooks()));
    }

    @GetMapping("/personalized")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getPersonalizedRecommendations(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(recommendationService.getPersonalizedRecommendations(jwt)));
    }
}
