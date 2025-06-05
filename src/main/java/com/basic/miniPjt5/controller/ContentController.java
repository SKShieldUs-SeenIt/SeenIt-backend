package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ContentDTO;
import com.basic.miniPjt5.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@Tag(name = "통합 검색", description = "영화와 드라마 통합 검색 API")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/search")
    @Operation(summary = "상세 검색", description = "POST 방식으로 상세 조건을 포함한 통합 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<ContentDTO.SearchResult> searchContent(
            @Valid @RequestBody ContentDTO.SearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        ContentDTO.SearchResult searchResult = contentService.searchContent(searchRequest, page, size);
        return ResponseEntity.ok(searchResult);
    }

    @GetMapping("/search")
    @Operation(summary = "간단 검색", description = "GET 방식으로 간단한 키워드 검색")
    public ResponseEntity<ContentDTO.SearchResult> searchContentByQuery(
            @Parameter(description = "검색 키워드", example = "아바타")
            @RequestParam String query,
            @Parameter(description = "콘텐츠 타입", example = "ALL")
            @RequestParam(defaultValue = "ALL") String contentType,
            @Parameter(description = "외부 API 사용 여부", example = "false")
            @RequestParam(defaultValue = "false") boolean useApi,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        ContentDTO.SearchRequest searchRequest = ContentDTO.SearchRequest.builder()
                .query(query)
                .contentType(contentType)
                .useApi(useApi)
                .sortBy("voteAverage")
                .sortDirection("desc")
                .build();

        ContentDTO.SearchResult searchResult = contentService.searchContent(searchRequest, page, size);
        return ResponseEntity.ok(searchResult);
    }
}