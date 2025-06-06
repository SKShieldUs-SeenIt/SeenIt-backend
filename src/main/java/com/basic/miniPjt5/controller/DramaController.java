package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.DTO.PageResponseDTO;
import com.basic.miniPjt5.entity.UserWatched;
import com.basic.miniPjt5.security.UserPrincipal;
import com.basic.miniPjt5.service.DramaService;
import com.basic.miniPjt5.service.UserWatchedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dramas")
@Tag(name = "드라마", description = "드라마 관련 API")
public class DramaController {

    private final DramaService dramaService;
    private final UserWatchedService userWatchedService;

    public DramaController(DramaService dramaService, UserWatchedService userWatchedService) {
        this.dramaService = dramaService;
        this.userWatchedService = userWatchedService;
    }

    @GetMapping
    @Operation(summary = "드라마 목록 조회", description = "페이징과 정렬을 지원하는 드라마 목록 조회")
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> getDramas(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "voteAverage")
            @RequestParam(defaultValue = "voteAverage") String sortBy,
            @Parameter(description = "정렬 방향", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<DramaDTO.ListResponse> dramaPage = dramaService.getDramas(page, size, sortBy, sortDirection);

        PageResponseDTO<DramaDTO.ListResponse> response = PageResponseDTO.<DramaDTO.ListResponse>builder()
                .content(dramaPage.getContent())
                .currentPage(dramaPage.getNumber())
                .totalPages(dramaPage.getTotalPages())
                .totalElements(dramaPage.getTotalElements())
                .size(dramaPage.getSize())
                .hasNext(dramaPage.hasNext())
                .hasPrevious(dramaPage.hasPrevious())
                .isFirst(dramaPage.isFirst())
                .isLast(dramaPage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "드라마 상세 조회", description = "ID로 특정 드라마의 상세 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "드라마를 찾을 수 없음")
    })
    public ResponseEntity<DramaDTO.Response> getDrama(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long id) {
        DramaDTO.Response drama = dramaService.getDramaById(id);
        return ResponseEntity.ok(drama);
    }

    @PostMapping
    @Operation(summary = "드라마 생성", description = "새로운 드라마 등록 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<DramaDTO.Response> createDrama(
            @Valid @RequestBody DramaDTO.CreateRequest request) {

        DramaDTO.Response createdDrama = dramaService.createDrama(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrama);
    }

    @PostMapping("/search")
    @Operation(summary = "드라마 상세 검색", description = "다양한 조건으로 드라마 검색")
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> searchDramas(
            @Valid @RequestBody DramaDTO.SearchRequest searchRequest,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Page<DramaDTO.ListResponse> dramaPage = dramaService.searchDramas(searchRequest, page, size);

        PageResponseDTO<DramaDTO.ListResponse> response = PageResponseDTO.<DramaDTO.ListResponse>builder()
                .content(dramaPage.getContent())
                .currentPage(dramaPage.getNumber())
                .totalPages(dramaPage.getTotalPages())
                .totalElements(dramaPage.getTotalElements())
                .size(dramaPage.getSize())
                .hasNext(dramaPage.hasNext())
                .hasPrevious(dramaPage.hasPrevious())
                .isFirst(dramaPage.isFirst())
                .isLast(dramaPage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "드라마 제목 검색", description = "제목으로 간단한 드라마 검색")
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> searchDramasByTitle(
            @Parameter(description = "검색할 제목", example = "오징어 게임")
            @RequestParam String title,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "first_air_date")
            @RequestParam(defaultValue = "first_air_date") String sortBy,
            @Parameter(description = "정렬 방향", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        DramaDTO.SearchRequest searchRequest = DramaDTO.SearchRequest.builder()
                .title(title)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<DramaDTO.ListResponse> dramaPage = dramaService.searchDramas(searchRequest, page, size);

        PageResponseDTO<DramaDTO.ListResponse> response = PageResponseDTO.<DramaDTO.ListResponse>builder()
                .content(dramaPage.getContent())
                .currentPage(dramaPage.getNumber())
                .totalPages(dramaPage.getTotalPages())
                .totalElements(dramaPage.getTotalElements())
                .size(dramaPage.getSize())
                .hasNext(dramaPage.hasNext())
                .hasPrevious(dramaPage.hasPrevious())
                .isFirst(dramaPage.isFirst())
                .isLast(dramaPage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/watched")
    @Operation(summary = "드라마 시청 완료 표시", description = "특정 드라마를 시청했다고 표시합니다.")
    public ResponseEntity<String> markDramaAsWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        userWatchedService.markAsWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, id);
        return ResponseEntity.ok("드라마를 시청 완료로 표시했습니다.");
    }

    @GetMapping("/{id}/watched")
    @Operation(summary = "드라마 시청 여부 확인", description = "특정 드라마의 시청 여부를 확인합니다.")
    public ResponseEntity<Boolean> checkDramaWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.ok(false);
        }

        boolean isWatched = userWatchedService.isWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, id);
        return ResponseEntity.ok(isWatched);
    }

    @DeleteMapping("/{id}/watched")
    @Operation(summary = "드라마 시청 기록 삭제", description = "특정 드라마의 시청 기록을 삭제합니다.")
    public ResponseEntity<String> removeDramaFromWatched(
            @Parameter(description = "드라마 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        userWatchedService.removeFromWatched(userPrincipal.getKakaoId(), UserWatched.ContentType.DRAMA, id);
        return ResponseEntity.ok("드라마 시청 기록을 삭제했습니다.");
    }
}