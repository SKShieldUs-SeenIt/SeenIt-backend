package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.entity.UserWatched;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.UserWatchedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/watched")
@Tag(name = "시청 기록", description = "사용자 시청 기록 관리 API")
@RequiredArgsConstructor
@Slf4j
public class WatchedController {

    private final UserWatchedService userWatchedService;

    // ===== 영화 시청 기록 API =====

    @PostMapping("/movies/{movieId}")
    @Operation(summary = "영화 시청 완료 표시", description = "특정 영화를 시청했다고 표시합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시청 완료 표시 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<String> markMovieAsWatched(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        userWatchedService.markAsWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.MOVIE, movieId);
        return ResponseEntity.ok("영화를 시청 완료로 표시했습니다.");
    }

    @GetMapping("/movies/{movieId}")
    @Operation(summary = "영화 시청 여부 확인", description = "특정 영화의 시청 여부를 확인합니다.")
    public ResponseEntity<Boolean> checkMovieWatched(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        if (userPrincipal == null) {
            return ResponseEntity.ok(false); // 로그인하지 않은 경우 false 반환
        }
        
        boolean isWatched = userWatchedService.isWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.MOVIE, movieId);
        return ResponseEntity.ok(isWatched);
    }

    @DeleteMapping("/movies/{movieId}")
    @Operation(summary = "영화 시청 기록 삭제", description = "특정 영화의 시청 기록을 삭제합니다.")
    public ResponseEntity<String> removeMovieFromWatched(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        userWatchedService.removeFromWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.MOVIE, movieId);
        return ResponseEntity.ok("영화 시청 기록을 삭제했습니다.");
    }

    // ===== 드라마 시청 기록 API =====

    @PostMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 시청 완료 표시", description = "특정 드라마를 시청했다고 표시합니다.")
    public ResponseEntity<String> markDramaAsWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        userWatchedService.markAsWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, dramaId);
        return ResponseEntity.ok("드라마를 시청 완료로 표시했습니다.");
    }

    @GetMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 시청 여부 확인", description = "특정 드라마의 시청 여부를 확인합니다.")
    public ResponseEntity<Boolean> checkDramaWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        if (userPrincipal == null) {
            return ResponseEntity.ok(false);
        }
        
        boolean isWatched = userWatchedService.isWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, dramaId);
        return ResponseEntity.ok(isWatched);
    }

    @DeleteMapping("/dramas/{dramaId}")
    @Operation(summary = "드라마 시청 기록 삭제", description = "특정 드라마의 시청 기록을 삭제합니다.")
    public ResponseEntity<String> removeDramaFromWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long dramaId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        userWatchedService.removeFromWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, dramaId);
        return ResponseEntity.ok("드라마 시청 기록을 삭제했습니다.");
    }

    // ===== 시청 목록 조회 API =====

    @GetMapping("/movies")
    @Operation(summary = "시청한 영화 목록", description = "사용자가 시청한 영화 목록을 조회합니다.")
    public ResponseEntity<Page<UserWatched>> getWatchedMovies(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        Page<UserWatched> watchedMovies = userWatchedService.getWatchedMovies(userPrincipal.getKakaoId(), page, size);
        return ResponseEntity.ok(watchedMovies);
    }

    @GetMapping("/dramas")
    @Operation(summary = "시청한 드라마 목록", description = "사용자가 시청한 드라마 목록을 조회합니다.")
    public ResponseEntity<Page<UserWatched>> getWatchedDramas(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        Page<UserWatched> watchedDramas = userWatchedService.getWatchedDramas(userPrincipal.getKakaoId(), page, size);
        return ResponseEntity.ok(watchedDramas);
    }

    @GetMapping("/all")
    @Operation(summary = "시청한 모든 콘텐츠 목록", description = "사용자가 시청한 모든 콘텐츠(영화+드라마) 목록을 조회합니다.")
    public ResponseEntity<Page<UserWatched>> getAllWatchedContents(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        Page<UserWatched> allWatched = userWatchedService.getAllWatchedContents(userPrincipal.getKakaoId(), page, size);
        return ResponseEntity.ok(allWatched);
    }

    // ===== ID 목록 조회 API (다른 서비스에서 활용 가능) =====

    @GetMapping("/movies/ids")
    @Operation(summary = "시청한 영화 ID 목록", description = "사용자가 시청한 영화의 ID 목록을 반환합니다.")
    public ResponseEntity<List<Long>> getWatchedMovieIds(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        List<Long> movieIds = userWatchedService.getWatchedMovieIds(userPrincipal.getKakaoId());
        return ResponseEntity.ok(movieIds);
    }

    @GetMapping("/dramas/ids")
    @Operation(summary = "시청한 드라마 ID 목록", description = "사용자가 시청한 드라마의 ID 목록을 반환합니다.")
    public ResponseEntity<List<Long>> getWatchedDramaIds(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        List<Long> dramaIds = userWatchedService.getWatchedDramaIds(userPrincipal.getKakaoId());
        return ResponseEntity.ok(dramaIds);
    }

    // ===== 통계 API =====

    @GetMapping("/stats")
    @Operation(summary = "시청 통계", description = "사용자의 시청 통계를 조회합니다.")
    public ResponseEntity<UserWatchedService.WatchedStats> getWatchedStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        validateUser(userPrincipal);
        UserWatchedService.WatchedStats stats = userWatchedService.getWatchedStats(userPrincipal.getKakaoId());
        return ResponseEntity.ok(stats);
    }

    private void validateUser(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
    }
}