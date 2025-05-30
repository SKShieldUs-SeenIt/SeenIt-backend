package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // 별점 생성/수정
    @PostMapping
    public ResponseEntity<RatingDTO.Response> createOrUpdateRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RatingDTO.Request requestDto) {

        Long userId = getUserId(userDetails);
        RatingDTO.Response response = ratingService.createOrUpdateRating(userId, requestDto);

        return ResponseEntity.ok(response);
    }

    // 별점 삭제
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long ratingId) {

        Long userId = getUserId(userDetails);
        ratingService.deleteRating(userId, ratingId);

        return ResponseEntity.noContent().build();
    }

    // 사용자가 준 별점 조회 (영화)
    @GetMapping("/my/movies/{movieId}")
    public ResponseEntity<RatingDTO.Response> getMyMovieRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long movieId) {

        Long userId = getUserId(userDetails);
        RatingDTO.Response rating = ratingService.getUserRating(userId, movieId, null);

        if (rating == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rating);
    }

    // 사용자가 준 별점 조회 (드라마)
    @GetMapping("/my/dramas/{dramaId}")
    public ResponseEntity<RatingDTO.Response> getMyDramaRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long dramaId) {

        Long userId = getUserId(userDetails);
        RatingDTO.Response rating = ratingService.getUserRating(userId, null, dramaId);

        if (rating == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rating);
    }

    // 영화 평균 별점 조회
    @GetMapping("/movies/{movieId}/average")
    public ResponseEntity<RatingDTO.AverageResponse> getMovieAverageRating(@PathVariable Long movieId) {
        RatingDTO.AverageResponse averageRating = ratingService.getMovieAverageRating(movieId);
        return ResponseEntity.ok(averageRating);
    }

    // 드라마 평균 별점 조회
    @GetMapping("/dramas/{dramaId}/average")
    public ResponseEntity<RatingDTO.AverageResponse> getDramaAverageRating(@PathVariable Long dramaId) {
        RatingDTO.AverageResponse averageRating = ratingService.getDramaAverageRating(dramaId);
        return ResponseEntity.ok(averageRating);
    }

    // 영화별 별점 목록 조회
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<Page<RatingDTO.Response>> getMovieRatings(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getMovieRatings(movieId, pageable);

        return ResponseEntity.ok(ratings);
    }

    // 드라마별 별점 목록 조회
    @GetMapping("/dramas/{dramaId}")
    public ResponseEntity<Page<RatingDTO.Response>> getDramaRatings(
            @PathVariable Long dramaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getDramaRatings(dramaId, pageable);

        return ResponseEntity.ok(ratings);
    }

    // 사용자별 별점 목록 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<RatingDTO.Response>> getUserRatings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getUserRatings(userId, pageable);

        return ResponseEntity.ok(ratings);
    }

    // 내가 준 별점 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Page<RatingDTO.Response>> getMyRatings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getUserId(userDetails);
        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getUserRatings(userId, pageable);

        return ResponseEntity.ok(ratings);
    }

    // 영화 별점 분포 조회
    @GetMapping("/movies/{movieId}/distribution")
    public ResponseEntity<Map<Integer, Long>> getMovieScoreDistribution(@PathVariable Long movieId) {
        Map<Integer, Long> distribution = ratingService.getScoreDistribution(movieId, null);
        return ResponseEntity.ok(distribution);
    }

    // 드라마 별점 분포 조회
    @GetMapping("/dramas/{dramaId}/distribution")
    public ResponseEntity<Map<Integer, Long>> getDramaScoreDistribution(@PathVariable Long dramaId) {
        Map<Integer, Long> distribution = ratingService.getScoreDistribution(null, dramaId);
        return ResponseEntity.ok(distribution);
    }

    // UserDetails에서 사용자 ID 추출하는 헬퍼 메서드
    private Long getUserId(UserDetails userDetails) {
        // 실제 구현에서는 UserDetails의 구현체에 따라 달라질 수 있습니다
        // 카카오 로그인을 사용하는 경우, CustomUserDetails에서 사용자 ID를 가져와야 합니다
        // 예: CustomUserDetails가 User 엔티티의 ID를 반환하도록 구현
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
    }
}