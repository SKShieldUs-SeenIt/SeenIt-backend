package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.GenreDTO;
import com.basic.miniPjt5.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "장르", description = "장르 관리 API")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @Operation(summary = "모든 장르 조회", description = "모든 장르의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "장르 목록 조회 성공")
    public ResponseEntity<List<GenreDTO.Response>> getAllGenres() {
        List<GenreDTO.Response> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/list")
    @Operation(summary = "장르 목록 조회", description = "장르의 간단한 정보 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "장르 목록 조회 성공")
    public ResponseEntity<List<GenreDTO.ListResponse>> getGenreList() {
        List<GenreDTO.ListResponse> genres = genreService.getGenreList();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "장르 상세 조회", description = "특정 장르의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장르 조회 성공"),
            @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음")
    })
    public ResponseEntity<GenreDTO.Response> getGenre(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long id) {
        GenreDTO.Response genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    @Operation(summary = "장르 생성", description = "새로운 장르를 생성합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장르 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<GenreDTO.Response> createGenre(
            @Valid @RequestBody GenreDTO.CreateRequest request) {

        GenreDTO.Response createdGenre = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @PutMapping("/{id}")
    @Operation(summary = "장르 수정", description = "기존 장르 정보를 수정합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장르 수정 성공"),
            @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<GenreDTO.Response> updateGenre(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long id,
            @Valid @RequestBody GenreDTO.UpdateRequest request) {

        GenreDTO.Response updatedGenre = genreService.updateGenre(id, request);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "장르 삭제", description = "장르를 삭제합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "장르 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "장르 통계 조회", description = "특정 장르의 통계 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "장르 통계 조회 성공")
    public ResponseEntity<GenreDTO.Statistics> getGenreStatistics(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long id) {
        GenreDTO.Statistics statistics = genreService.getGenreStatistics(id);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{id}/popular")
    @Operation(summary = "장르별 인기 컨텐츠", description = "특정 장르의 인기 컨텐츠를 조회합니다")
    @ApiResponse(responseCode = "200", description = "인기 컨텐츠 조회 성공")
    public ResponseEntity<GenreDTO.PopularContent> getPopularContentByGenre(
            @Parameter(description = "장르 ID", example = "28")
            @PathVariable Long id,
            @Parameter(description = "조회할 컨텐츠 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        GenreDTO.PopularContent popularContent = genreService.getPopularContentByGenre(id, limit);
        return ResponseEntity.ok(popularContent);
    }

    @PostMapping("/sync")
    @Operation(summary = "TMDB 장르 동기화", description = "TMDB에서 장르 정보를 동기화합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장르 동기화 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "외부 API 오류")
    })
    public ResponseEntity<List<GenreDTO.Response>> syncGenresFromTMDB() {
        List<GenreDTO.Response> syncedGenres = genreService.syncGenresFromTMDB();
        return ResponseEntity.ok(syncedGenres);
    }

    @GetMapping("/search")
    @Operation(summary = "장르 검색", description = "장르명으로 장르를 검색합니다")
    @ApiResponse(responseCode = "200", description = "장르 검색 성공")
    public ResponseEntity<List<GenreDTO.ListResponse>> searchGenres(
            @Parameter(description = "검색할 장르명", example = "액션")
            @RequestParam String name) {

        List<GenreDTO.ListResponse> genres = genreService.searchGenresByName(name);
        return ResponseEntity.ok(genres);
    }
}