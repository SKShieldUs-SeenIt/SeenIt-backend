package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.DTO.PageResponseDTO;
import com.basic.miniPjt5.service.DramaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dramas")
@CrossOrigin(origins = "*")
public class DramaController {

    private final DramaService dramaService;

    public DramaController(DramaService dramaService) {
        this.dramaService = dramaService;
    }

    // 드라마 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> getDramas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "voteAverage") String sortBy,
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

    // 드라마 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DramaDTO.Response> getDrama(@PathVariable Long id) {
        DramaDTO.Response drama = dramaService.getDramaById(id);
        return ResponseEntity.ok(drama);
    }

    // 드라마 생성 (관리자용)
    @PostMapping
    public ResponseEntity<DramaDTO.Response> createDrama(
            @Valid @RequestBody DramaDTO.CreateRequest request) {
        
        DramaDTO.Response createdDrama = dramaService.createDrama(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrama);
    }

    // 드라마 검색
    @PostMapping("/search")
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> searchDramas(
            @Valid @RequestBody DramaDTO.SearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
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

    // GET 방식 드라마 검색
    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<DramaDTO.ListResponse>> searchDramasByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "first_air_date") String sortBy,
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
}