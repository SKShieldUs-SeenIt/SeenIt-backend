package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ReviewDTO;
import com.basic.miniPjt5.security.JwtAuthenticationHelper;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "리뷰 관리 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtAuthenticationHelper jwtAuthenticationHelper;

    @PostMapping
    @Operation(summary = "리뷰 생성", description = "새로운 리뷰 작성")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<ReviewDTO.Response> createReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ReviewDTO.CreateRequest requestDto) {

        Long userId = userPrincipal.getId();  // 직접 접근
        ReviewDTO.Response response = reviewService.createReview(userId, requestDto);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "기존 리뷰 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ReviewDTO.Response> updateReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 ID", example = "1")
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTO.UpdateRequest requestDto) {

        Long userId = jwtAuthenticationHelper.extractUserId(userPrincipal);
        ReviewDTO.Response response = reviewService.updateReview(userId, reviewId, requestDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "리뷰 ID", example = "1")
            @PathVariable Long reviewId) {

        Long userId = jwtAuthenticationHelper.extractUserId(userPrincipal);
        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보 조회")
    public ResponseEntity<ReviewDTO.Response> getReview(
            @Parameter(description = "리뷰 ID", example = "1")
            @PathVariable Long reviewId) {
        ReviewDTO.Response response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movies/{movieId}")
    @Operation(summary = "영화 리뷰 목록", description = "특정 영화의 모든 리뷰 목록 조회")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getMovieReviews(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getMovieReviews(movieId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 리뷰 목록", description = "특정 드라마의 모든 리뷰 목록 조회")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getDramaReviews(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getDramaReviews(dramaId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 리뷰 목록", description = "특정 사용자의 모든 리뷰 목록 조회")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getUserReviews(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my")
    @Operation(summary = "내 리뷰 목록", description = "내가 작성한 모든 리뷰 목록 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getMyReviews(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Long userId = jwtAuthenticationHelper.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/latest")
    @Operation(summary = "최신 리뷰 목록", description = "최근에 작성된 리뷰 목록 조회")
    public ResponseEntity<Page<ReviewDTO.ListResponse>> getLatestReviews(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ListResponse> reviews = reviewService.getLatestReviews(pageable);

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/movies/{movieId}/count")
    public ResponseEntity<Long> getMovieReviewCount(Long movieId) {
        Long count = reviewService.countMovieReviews(movieId);
        return ResponseEntity.ok(count);
    }
}