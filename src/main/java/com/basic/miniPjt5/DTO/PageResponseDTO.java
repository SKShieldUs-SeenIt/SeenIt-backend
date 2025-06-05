package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "페이징 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDTO<T> {

    @Schema(description = "데이터 목록")
    private List<T> content;

    @Schema(description = "현재 페이지", example = "0")
    private Integer currentPage;

    @Schema(description = "전체 페이지 수", example = "10")
    private Integer totalPages;

    @Schema(description = "전체 데이터 수", example = "95")
    private Long totalElements;

    @Schema(description = "페이지 크기", example = "10")
    private Integer size;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;

    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private Boolean hasPrevious;

    @Schema(description = "첫 페이지 여부", example = "true")
    private Boolean isFirst;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private Boolean isLast;
}