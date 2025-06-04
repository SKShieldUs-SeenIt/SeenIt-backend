package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.security.JwtAuthenticationHelper;
import com.basic.miniPjt5.service.RatingStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "통계", description = "평점 및 통계 관련 API")
public class RatingStatisticsController {

    private final RatingStatisticsService statisticsService;
    private final JwtAuthenticationHelper jwtAuthenticationHelper;

    @GetMapping("/movies/{movieId}")
    @Operation(summary = "영화 평점 통계", description = "특정 영화의 상세 평점 통계 조회")
    public ResponseEntity<RatingDTO.StatisticsResponse> getMovieStatistics(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId) {
        RatingDTO.StatisticsResponse statistics = statisticsService.getMovieStatistics(movieId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 평점 통계", description = "특정 드라마의 상세 평점 통계 조회")
    public ResponseEntity<RatingDTO.StatisticsResponse> getDramaStatistics(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId) {
        RatingDTO.StatisticsResponse statistics = statisticsService.getDramaStatistics(dramaId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/top-rated/movies")
    @Operation(summary = "평점 높은 영화", description = "평점이 높은 영화 목록 조회")
    public ResponseEntity<Page<RatingDTO.SimpleRating>> getTopRatedMovies(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.SimpleRating> topRatedMovies = statisticsService.getTopRatedMovies(pageable);

        return ResponseEntity.ok(topRatedMovies);
    }

    @GetMapping("/top-rated/dramas")
    @Operation(summary = "평점 높은 드라마", description = "평점이 높은 드라마 목록 조회")
    public ResponseEntity<Page<RatingDTO.SimpleRating>> getTopRatedDramas(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.SimpleRating> topRatedDramas = statisticsService.getTopRatedDramas(pageable);

        return ResponseEntity.ok(topRatedDramas);
    }

    @GetMapping("/recently-popular")
    @Operation(summary = "최근 인기 작품", description = "최근 평점이 많이 등록된 인기 작품들 조회")
    public ResponseEntity<List<RatingDTO.SimpleRating>> getRecentlyPopularContents(
            @Parameter(description = "조회할 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        List<RatingDTO.SimpleRating> popularContents = statisticsService.getRecentlyPopularContents(limit);
        return ResponseEntity.ok(popularContents);
    }

    @GetMapping("/genres/average-rating")
    @Operation(summary = "장르별 평균 평점", description = "장르별 평균 평점 통계 조회")
    public ResponseEntity<List<Map<String, Object>>> getAverageRatingByGenre() {
        List<Map<String, Object>> genreStats = statisticsService.getAverageRatingByGenre();
        return ResponseEntity.ok(genreStats);
    }

    @GetMapping("/my")
    @Operation(summary = "개인 평점 통계", description = "내 평점 통계 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getMyRatingStatistics() {
        Long userId = jwtAuthenticationHelper.getCurrentUserId();
        Map<String, Object> userStats = statisticsService.getUserRatingStatistics(userId);

        return ResponseEntity.ok(userStats);
    }
}