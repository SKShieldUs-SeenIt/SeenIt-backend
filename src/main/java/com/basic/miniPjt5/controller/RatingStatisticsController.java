package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.security.JwtAuthenticationHelper;
import com.basic.miniPjt5.service.RatingStatisticsService;
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
public class RatingStatisticsController {

    private final RatingStatisticsService statisticsService;
    private final JwtAuthenticationHelper jwtAuthenticationHelper;

    // 영화 평점 통계 상세 조회
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<RatingDTO.StatisticsResponse> getMovieStatistics(@PathVariable Long movieId) {
        RatingDTO.StatisticsResponse statistics = statisticsService.getMovieStatistics(movieId);
        return ResponseEntity.ok(statistics);
    }

    // 드라마 평점 통계 상세 조회
    @GetMapping("/dramas/{dramaId}")
    public ResponseEntity<RatingDTO.StatisticsResponse> getDramaStatistics(@PathVariable Long dramaId) {
        RatingDTO.StatisticsResponse statistics = statisticsService.getDramaStatistics(dramaId);
        return ResponseEntity.ok(statistics);
    }

    // 평점 높은 영화 목록 조회
    @GetMapping("/top-rated/movies")
    public ResponseEntity<Page<RatingDTO.SimpleRating>> getTopRatedMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.SimpleRating> topRatedMovies = statisticsService.getTopRatedMovies(pageable);

        return ResponseEntity.ok(topRatedMovies);
    }

    // 평점 높은 드라마 목록 조회
    @GetMapping("/top-rated/dramas")
    public ResponseEntity<Page<RatingDTO.SimpleRating>> getTopRatedDramas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.SimpleRating> topRatedDramas = statisticsService.getTopRatedDramas(pageable);

        return ResponseEntity.ok(topRatedDramas);
    }

    // 최근 인기 작품들 조회 (평점이 많이 등록된 작품들)
    @GetMapping("/recently-popular")
    public ResponseEntity<List<RatingDTO.SimpleRating>> getRecentlyPopularContents(
            @RequestParam(defaultValue = "10") int limit) {

        List<RatingDTO.SimpleRating> popularContents = statisticsService.getRecentlyPopularContents(limit);
        return ResponseEntity.ok(popularContents);
    }

    // 장르별 평균 평점 조회
    @GetMapping("/genres/average-rating")
    public ResponseEntity<List<Map<String, Object>>> getAverageRatingByGenre() {
        List<Map<String, Object>> genreStats = statisticsService.getAverageRatingByGenre();
        return ResponseEntity.ok(genreStats);
    }

    // 개인 평점 통계 조회
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyRatingStatistics() {
        Long userId = jwtAuthenticationHelper.getCurrentUserId();
        Map<String, Object> userStats = statisticsService.getUserRatingStatistics(userId);

        return ResponseEntity.ok(userStats);
    }
}