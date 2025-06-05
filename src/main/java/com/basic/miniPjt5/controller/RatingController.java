package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.RatingService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "별점", description = "별점 관리 API")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @Operation(summary = "별점 등록/수정", description = "작품에 대한 별점 등록 또는 수정")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "별점 등록/수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<RatingDTO.Response> createOrUpdateRating(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RatingDTO.Request requestDto) {

        Long userId = userPrincipal.getId();
        RatingDTO.Response response = ratingService.createOrUpdateRating(userId, requestDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ratingId}")
    @Operation(summary = "별점 삭제", description = "등록한 별점 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "별점 ID", example = "1")
            @PathVariable Long ratingId) {

        Long userId = userPrincipal.getId();
        ratingService.deleteRating(userId, ratingId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/movies/{movieId}")
    @Operation(summary = "내 영화 별점 조회", description = "특정 영화에 대한 내 별점 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingDTO.Response> getMyMovieRating(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId) {

        Long userId = userPrincipal.getId();
        RatingDTO.Response rating = ratingService.getUserRating(userId, movieId, null);

        if (rating == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rating);
    }

    @GetMapping("/my/dramas/{dramaId}")
    @Operation(summary = "내 드라마 별점 조회", description = "특정 드라마에 대한 내 별점 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingDTO.Response> getMyDramaRating(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId) {

        Long userId = userPrincipal.getId();
        RatingDTO.Response rating = ratingService.getUserRating(userId, null, dramaId);

        if (rating == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rating);
    }

    @GetMapping("/movies/{movieId}/average")
    @Operation(summary = "영화 평균 별점", description = "특정 영화의 평균 별점 조회")
    public ResponseEntity<RatingDTO.AverageResponse> getMovieAverageRating(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId) {
        RatingDTO.AverageResponse averageRating = ratingService.getMovieAverageRating(movieId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/dramas/{dramaId}/average")
    @Operation(summary = "드라마 평균 별점", description = "특정 드라마의 평균 별점 조회")
    public ResponseEntity<RatingDTO.AverageResponse> getDramaAverageRating(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId) {
        RatingDTO.AverageResponse averageRating = ratingService.getDramaAverageRating(dramaId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/movies/{movieId}")
    @Operation(summary = "영화 별점 목록", description = "특정 영화의 모든 별점 목록 조회")
    public ResponseEntity<Page<RatingDTO.Response>> getMovieRatings(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getMovieRatings(movieId, pageable);

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/my")
    @Operation(summary = "내 별점 목록", description = "내가 등록한 모든 별점 목록 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<RatingDTO.Response>> getMyRatings(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userPrincipal.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getUserRatings(userId, pageable);

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 별점 목록", description = "특정 드라마의 모든 별점 목록 조회")
    public ResponseEntity<Page<RatingDTO.Response>> getDramaRatings(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getDramaRatings(dramaId, pageable);

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 별점 목록", description = "특정 사용자의 모든 별점 목록 조회")
    public ResponseEntity<Page<RatingDTO.Response>> getUserRatings(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RatingDTO.Response> ratings = ratingService.getUserRatings(userId, pageable);

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/movies/{movieId}/distribution")
    @Operation(summary = "영화 별점 분포", description = "특정 영화의 별점 분포 조회")
    public ResponseEntity<Map<String, Long>> getMovieScoreDistribution(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId) {
        Map<String, Long> distribution = ratingService.getScoreDistribution(movieId, null);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/dramas/{dramaId}/distribution")
    @Operation(summary = "드라마 별점 분포", description = "특정 드라마의 별점 분포 조회")
    public ResponseEntity<Map<String, Long>> getDramaScoreDistribution(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId) {
        Map<String, Long> distribution = ratingService.getScoreDistribution(null, dramaId);
        return ResponseEntity.ok(distribution);
    }
}