package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.ContentDTO;
import com.basic.miniPjt5.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    // 통합 검색
    @PostMapping("/search")
    public ResponseEntity<ContentDTO.SearchResult> searchContent(
            @Valid @RequestBody ContentDTO.SearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ContentDTO.SearchResult searchResult = contentService.searchContent(searchRequest, page, size);
        return ResponseEntity.ok(searchResult);
    }

    // GET 방식 통합 검색 (간단한 검색)
    @GetMapping("/search")
    public ResponseEntity<ContentDTO.SearchResult> searchContentByQuery(
            @RequestParam String query,
            @RequestParam(defaultValue = "ALL") String contentType,
            @RequestParam(defaultValue = "false") boolean useApi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ContentDTO.SearchRequest searchRequest = ContentDTO.SearchRequest.builder()
                .query(query)
                .contentType(contentType)
                .useApi(useApi)
                .sortBy("popularity")
                .sortDirection("desc")
                .build();

        ContentDTO.SearchResult searchResult = contentService.searchContent(searchRequest, page, size);
        return ResponseEntity.ok(searchResult);
    }
}