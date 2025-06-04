package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.GenreDTO;
import com.basic.miniPjt5.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // 모든 장르 조회 (상세 정보 포함)
    @GetMapping
    public ResponseEntity<List<GenreDTO.Response>> getAllGenres() {
        List<GenreDTO.Response> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    // 장르 목록 조회 (간단한 정보)
    @GetMapping("/list")
    public ResponseEntity<List<GenreDTO.ListResponse>> getGenreList() {
        List<GenreDTO.ListResponse> genres = genreService.getGenreList();
        return ResponseEntity.ok(genres);
    }

    // 장르 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO.Response> getGenre(@PathVariable Long id) {
        GenreDTO.Response genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    // 장르 생성 (관리자용)
    @PostMapping
    public ResponseEntity<GenreDTO.Response> createGenre(
            @Valid @RequestBody GenreDTO.CreateRequest request) {

        GenreDTO.Response createdGenre = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    // 장르 수정 (관리자용)
    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO.Response> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreDTO.UpdateRequest request) {

        GenreDTO.Response updatedGenre = genreService.updateGenre(id, request);
        return ResponseEntity.ok(updatedGenre);
    }

    // 장르 삭제 (관리자용)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    // 장르 통계 조회
    @GetMapping("/{id}/statistics")
    public ResponseEntity<GenreDTO.Statistics> getGenreStatistics(@PathVariable Long id) {
        GenreDTO.Statistics statistics = genreService.getGenreStatistics(id);
        return ResponseEntity.ok(statistics);
    }

    // 장르별 인기 컨텐츠 조회
    @GetMapping("/{id}/popular")
    public ResponseEntity<GenreDTO.PopularContent> getPopularContentByGenre(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {

        GenreDTO.PopularContent popularContent = genreService.getPopularContentByGenre(id, limit);
        return ResponseEntity.ok(popularContent);
    }

    // TMDB 장르 동기화 (관리자용)
    @PostMapping("/sync")
    public ResponseEntity<List<GenreDTO.Response>> syncGenresFromTMDB() {
        List<GenreDTO.Response> syncedGenres = genreService.syncGenresFromTMDB();
        return ResponseEntity.ok(syncedGenres);
    }

    // 장르명으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<GenreDTO.ListResponse>> searchGenres(
            @RequestParam String name) {

        List<GenreDTO.ListResponse> genres = genreService.searchGenresByName(name);
        return ResponseEntity.ok(genres);
    }
}