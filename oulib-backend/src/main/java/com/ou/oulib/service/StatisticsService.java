package com.ou.oulib.service;

import com.ou.oulib.dto.response.statistics.*;
import com.ou.oulib.enums.BookCopyStatus;
import com.ou.oulib.enums.BorrowStatus;
import com.ou.oulib.repository.BookCopyRepository;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.BorrowRecordRepository;
import com.ou.oulib.repository.UserRepository;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.PageResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

    BookRepository bookRepository;
    BookCopyRepository bookCopyRepository;
    BorrowRecordRepository borrowRecordRepository;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public InventorySummaryResponse getInventorySummary() {
        long totalBooks = bookRepository.count();
        long totalBookCopies = bookCopyRepository.count();
        long availableCopies = bookCopyRepository.countByStatus(BookCopyStatus.AVAILABLE);
        long borrowedCopies = bookCopyRepository.countByStatus(BookCopyStatus.BORROWED);

        double availabilityRate = totalBookCopies == 0
                ? 0.0
                : (double) availableCopies / totalBookCopies;

        List<CategoryDistributionResponse> categoryDistribution = bookRepository.getCategoryDistribution();

        return InventorySummaryResponse.builder()
                .totalBooks(totalBooks)
                .totalBookCopies(totalBookCopies)
                .availableCopies(availableCopies)
                .borrowedCopies(borrowedCopies)
                .availabilityRate(availabilityRate)
                .categoryDistribution(categoryDistribution)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<OverdueUserResponse> getOverdueRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime now = LocalDateTime.now();

        Page<OverdueUserInfoResponse> overdueUsersPage = borrowRecordRepository.findOverdueUsers(
                BorrowStatus.BORROWING,
                now,
                pageable
        );

        if (overdueUsersPage.isEmpty()) {
            return PageResponseUtils.build(overdueUsersPage, user -> OverdueUserResponse.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .overdueBooks(List.of())
                    .build());
        }

        List<String> userIds = overdueUsersPage.getContent()
                .stream()
                .map(OverdueUserInfoResponse::getUserId)
                .toList();

        List<OverdueBookDetailResponse> overdueDetails = borrowRecordRepository.findOverdueDetailsByUserIds(
                BorrowStatus.BORROWING,
                now,
                userIds
        );

        LocalDate today = LocalDate.now();
        Map<String, List<OverdueBookItemResponse>> overdueBooksByUserId = overdueDetails.stream()
                .collect(Collectors.groupingBy(
                        OverdueBookDetailResponse::getUserId,
                        Collectors.mapping(detail -> {
                            LocalDate dueDate = detail.getDueDate().toLocalDate();
                            long overdueDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, today));

                            return OverdueBookItemResponse.builder()
                                    .bookTitle(detail.getBookTitle())
                                    .barcode(detail.getBarcode())
                                    .dueDate(dueDate)
                                    .overdueDays(overdueDays)
                                    .build();
                        }, Collectors.toList())
                ));

        List<OverdueUserResponse> content = overdueUsersPage.getContent().stream()
                .map(user -> OverdueUserResponse.builder()
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .overdueBooks(overdueBooksByUserId.getOrDefault(user.getUserId(), List.of()))
                        .build())
                .toList();

        Page<OverdueUserResponse> mappedPage = new PageImpl<>(
                content,
                pageable,
                overdueUsersPage.getTotalElements()
        );

        return PageResponseUtils.build(mappedPage, Function.identity());
    }

    @Transactional(readOnly = true)
    public List<BorrowingActivityResponse> getBorrowingActivity(LocalDate from, LocalDate to, String groupBy) {
        if (from == null || to == null || from.isAfter(to)) {
            return List.of();
        }

        TimeGroup timeGroup = TimeGroup.from(groupBy);

        Map<String, Long> borrowCounts = aggregateByGroup(
                borrowRecordRepository.countBorrowActivitiesByDateRange(from, to),
                timeGroup
        );
        Map<String, Long> returnCounts = aggregateByGroup(
                borrowRecordRepository.countReturnActivitiesByDateRange(from, to),
                timeGroup
        );

        LinkedHashMap<String, BorrowingActivityResponse> timeline = initTimeline(from, to, timeGroup);

        for (Map.Entry<String, Long> entry : borrowCounts.entrySet()) {
            BorrowingActivityResponse existing = timeline.get(entry.getKey());
            if (existing != null) {
                existing.setBorrowCount(entry.getValue());
            }
        }

        for (Map.Entry<String, Long> entry : returnCounts.entrySet()) {
            BorrowingActivityResponse existing = timeline.get(entry.getKey());
            if (existing != null) {
                existing.setReturnCount(entry.getValue());
            }
        }

        return new ArrayList<>(timeline.values());
    }

    @Transactional(readOnly = true)
    public List<TopBorrowedBookResponse> getTopBorrowedBooks(int limit) {
        int safeLimit = limit <= 0 ? 10 : limit;
        Pageable pageable = PageRequest.of(0, safeLimit);
        return borrowRecordRepository.findTopBorrowedBooks(pageable);
    }

    @Transactional(readOnly = true)
    public ActiveUsersStatisticsResponse getActiveUsersStatistics(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            return ActiveUsersStatisticsResponse.builder()
                    .activeUsers(0)
                    .newUsers(0)
                    .build();
        }

        long activeUsers = borrowRecordRepository.countDistinctActiveUsersByBorrowDateRange(from, to);

        Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        long newUsers = userRepository.countByCreatedAtBetween(fromInstant, toInstant);

        return ActiveUsersStatisticsResponse.builder()
                .activeUsers(activeUsers)
                .newUsers(newUsers)
                .build();
    }

    @Transactional(readOnly = true)
    public SystemTotalsResponse getSystemTotals() {
        long totalUsers = userRepository.count();
        long totalBooks = bookRepository.count();
        long totalCopies = bookCopyRepository.count();
        long totalBorrowRecords = borrowRecordRepository.count();
        long totalCurrentlyBorrowed = borrowRecordRepository.countByStatus(BorrowStatus.BORROWING);
        long totalOverdue = borrowRecordRepository.countByStatus(BorrowStatus.OVERDUE);

        return SystemTotalsResponse.builder()
                .totalUsers(totalUsers)
                .totalBooks(totalBooks)
                .totalCopies(totalCopies)
                .totalBorrowRecords(totalBorrowRecords)
                .totalCurrentlyBorrowed(totalCurrentlyBorrowed)
                .totalOverdue(totalOverdue)
                .build();
    }

    private Map<String, Long> aggregateByGroup(List<DailyCountResponse> rows, TimeGroup group) {
        return rows.stream()
                .filter(row -> row.getDate() != null)
                .collect(Collectors.groupingBy(
                        row -> formatTimeKey(row.getDate(), group),
                        LinkedHashMap::new,
                        Collectors.summingLong(DailyCountResponse::getCount)
                ));
    }

    private LinkedHashMap<String, BorrowingActivityResponse> initTimeline(LocalDate from, LocalDate to, TimeGroup group) {
        LinkedHashMap<String, BorrowingActivityResponse> timeline = new LinkedHashMap<>();

        switch (group) {
            case DAY -> {
                LocalDate current = from;
                while (!current.isAfter(to)) {
                    String key = current.toString();
                    timeline.put(key, BorrowingActivityResponse.builder()
                            .time(key)
                            .borrowCount(0)
                            .returnCount(0)
                            .build());
                    current = current.plusDays(1);
                }
            }
            case WEEK -> {
                LocalDate current = from.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate end = to.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                while (!current.isAfter(end)) {
                    String key = current.toString();
                    timeline.put(key, BorrowingActivityResponse.builder()
                            .time(key)
                            .borrowCount(0)
                            .returnCount(0)
                            .build());
                    current = current.plusWeeks(1);
                }
            }
            case MONTH -> {
                YearMonth current = YearMonth.from(from);
                YearMonth end = YearMonth.from(to);

                while (!current.isAfter(end)) {
                    String key = current.toString();
                    timeline.put(key, BorrowingActivityResponse.builder()
                            .time(key)
                            .borrowCount(0)
                            .returnCount(0)
                            .build());
                    current = current.plusMonths(1);
                }
            }
        }

        return timeline;
    }

    private String formatTimeKey(LocalDate date, TimeGroup group) {
        return switch (group) {
            case DAY -> date.toString();
            case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
            case MONTH -> YearMonth.from(date).toString();
        };
    }

    private enum TimeGroup {
        DAY,
        WEEK,
        MONTH;

        static TimeGroup from(String value) {
            if (value == null || value.isBlank()) {
                return DAY;
            }

            return switch (value.trim().toLowerCase()) {
                case "week" -> WEEK;
                case "month" -> MONTH;
                default -> DAY;
            };
        }
    }
}
