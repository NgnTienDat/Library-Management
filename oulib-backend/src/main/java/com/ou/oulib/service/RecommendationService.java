package com.ou.oulib.service;

import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.entity.Book;
import com.ou.oulib.mapper.BookMapper;
import com.ou.oulib.repository.RecommendationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RecommendationService {

    RecommendationRepository recommendationRepository;
    BookMapper bookMapper;

    static final int RECOMMENDATION_LIMIT = 10;
    static final Pageable TOP_10 = PageRequest.of(0, 10);
    static final Pageable TOP_1 = PageRequest.of(0, 1);

    @Transactional(readOnly = true)
    public List<BookResponse> getPopularBooks() {
        List<String> rankedIds = recommendationRepository.findPopularBookIds(TOP_10);
        System.out.println("Popular book IDs: " + rankedIds);
        return loadBooksInRankOrder(rankedIds)
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getTrendingBooks() {
        LocalDate since = LocalDate.now().minusDays(1);
        List<String> rankedIds = recommendationRepository.findTrendingBookIds(since, TOP_10);
        return loadBooksInRankOrder(rankedIds)
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getPersonalizedRecommendations(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email = jwt.getSubject();

        List<String> borrowedBookIds = recommendationRepository.findBorrowedBookIdsByUserEmail(email);
        System.out.println("Borrowed book IDs for user " + email + ": " + borrowedBookIds);

        // Cold-start: user has no borrow history, fallback to popular books
        if (borrowedBookIds.isEmpty()) {
            return getPopularBooks();
        }

        Map<String, Book> recommendations = new LinkedHashMap<>();

        // Try category-based recommendations first
        List<String> topCategoryIds = recommendationRepository.findTopCategoryIdsByUserEmail(email, TOP_1);
        System.out.println("Borrowed: " + borrowedBookIds);
        System.out.println("Top category: " + topCategoryIds);
        if (!topCategoryIds.isEmpty()) {
            recommendationRepository.findByCategoryExcludingBookIds(topCategoryIds.get(0), borrowedBookIds, TOP_10)
                    .forEach(book -> recommendations.putIfAbsent(book.getId(), book));

        }


        // Fill remaining slots with author-based recommendations
//        if (recommendations.size() < RECOMMENDATION_LIMIT) {
//            List<String> topAuthorIds = recommendationRepository.findTopAuthorIdsByUserEmail(email, TOP_1);
//            if (!topAuthorIds.isEmpty()) {
//                int remaining = RECOMMENDATION_LIMIT - recommendations.size();
//                recommendationRepository.findByAuthorExcludingBookIds(topAuthorIds.get(0), borrowedBookIds,
//                                PageRequest.of(0, remaining))
//                        .forEach(book -> recommendations.putIfAbsent(book.getId(), book));
//            }
//        }

        // If no personalized signal yields results, fallback to global popular books.
        if (recommendations.isEmpty()) {
            return getPopularBooks();
        }

        // Fill remaining slots from popular books while excluding already-borrowed items.
        if (recommendations.size() < RECOMMENDATION_LIMIT) {
            int remaining = RECOMMENDATION_LIMIT - recommendations.size();
            List<String> rankedFallbackIds = recommendationRepository.findPopularBookIdsExcludingBookIds(
                    borrowedBookIds,
                    PageRequest.of(0, remaining)
            );

            loadBooksInRankOrder(rankedFallbackIds)
                    .forEach(book -> recommendations.putIfAbsent(book.getId(), book));
        }

        return recommendations.values().stream()
                .limit(RECOMMENDATION_LIMIT)
                .map(bookMapper::toBookResponse)
                .toList();
    }

    private List<Book> loadBooksInRankOrder(List<String> rankedIds) {
        if (rankedIds == null || rankedIds.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> rankIndex = new LinkedHashMap<>();
        for (int i = 0; i < rankedIds.size(); i++) {
            rankIndex.putIfAbsent(rankedIds.get(i), i);
        }

        Set<String> uniqueIds = new HashSet<>(rankIndex.keySet());

        return recommendationRepository.findByIdIn(List.copyOf(uniqueIds)).stream()
                .sorted(Comparator.comparingInt(book -> rankIndex.getOrDefault(book.getId(), Integer.MAX_VALUE)))
                .collect(Collectors.toList());
    }
}
