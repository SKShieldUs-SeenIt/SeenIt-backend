package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ReviewDTO;
import com.basic.miniPjt5.security.JwtAuthenticationHelper;
import com.basic.miniPjt5.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtAuthenticationHelper jwtAuthenticationHelper;

    // 리뷰 생성
    @PostMapping
    public ResponseEntity<ReviewDTO.Response> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewDTO.CreateRequest requestDto) {

        Long userId = jwtAuthenticationHelper.extractUserId(userDetails);
        ReviewDTO.Response response = reviewService.createReview(userId, requestDto);

        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO.Response> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTO.UpdateRequest requestDto) {

        Long userId = jwtAuthenticationHelper.extractUserId(userDetails);
        ReviewDTO.Response response = reviewService.updateReview(userId, reviewId, requestDto);

        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reviewId) {

        Long userId = jwtAuthenticationHelper.extractUserId(userDetails);
        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }

    // 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO.Response> getReview(@PathVariable Long reviewId) {
        ReviewDTO.Response response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    // 영화별 리뷰 목록 조회
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getMovieReviews(movieId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 드라마별 리뷰 목록 조회
    @GetMapping("/dramas/{dramaId}")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getDramaReviews(
            @PathVariable Long dramaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getDramaReviews(dramaId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 사용자별 리뷰 목록 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 내가 작성한 리뷰 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = jwtAuthenticationHelper.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 최신 리뷰 목록 조회
    @GetMapping("/latest")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getLatestReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getLatestReviews(pageable);

        return ResponseEntity.ok(reviews);
    }
}