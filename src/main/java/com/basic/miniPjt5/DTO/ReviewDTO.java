package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class ReviewDTO {

    @Schema(description = "리뷰 생성 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @Schema(description = "리뷰 내용", example = "스토리가 탄탄하고 연출이 훌륭했습니다...", required = true)
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "내용은 10-2000자 사이여야 합니다.")
        private String content;

        @Schema(description = "영화 ID (영화 리뷰시 필수)", example = "1")
        private Long movieId;

        @Schema(description = "드라마 ID (드라마 리뷰시 필수)", example = "1")
        private Long dramaId;

        @Schema(description = "스포일러 포함 여부", example = "false")
        @Builder.Default
        private Boolean isSpoiler = false;
    }

    @Schema(description = "리뷰 수정 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Schema(description = "리뷰 내용", example = "스토리가 탄탄하고 연출이 훌륭했습니다...", required = true)
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "내용은 10-2000자 사이여야 합니다.")
        private String content;

        @Schema(description = "스포일러 포함 여부", example = "false")
        @Builder.Default
        private Boolean isSpoiler = false;
    }

    @Schema(description = "리뷰 상세 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "리뷰 ID", example = "1")
        private Long id;

        @Schema(description = "리뷰 내용", example = "스토리가 탄탄하고 연출이 훌륭했습니다...")
        private String content;

        @Schema(description = "작성자명", example = "홍길동")
        private String username;

        @Schema(description = "작성자 ID", example = "123")
        private Long userId;

        @Schema(description = "좋아요 수", example = "15")
        private Integer likesCount;

        @Schema(description = "스포일러 포함 여부", example = "false")
        private Boolean isSpoiler;

        @Schema(description = "영화 ID", example = "1")
        private Long movieId;

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        private String movieTitle;

        @Schema(description = "영화 포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String moviePosterPath;

        @Schema(description = "드라마 ID", example = "1")
        private Long dramaId;

        @Schema(description = "드라마 제목", example = "오징어 게임")
        private String dramaTitle;

        @Schema(description = "드라마 포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String dramaPosterPath;

        @Schema(description = "콘텐츠 타입", example = "MOVIE", allowableValues = {"MOVIE", "DRAMA"})
        private String contentType;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }

    @Schema(description = "리뷰 목록용 간단한 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        @Schema(description = "리뷰 ID", example = "1")
        private Long id;

        @Schema(description = "리뷰 내용 (요약)", example = "스토리가 탄탄하고...")
        private String content;

        @Schema(description = "작성자명", example = "홍길동")
        private String username;

        @Schema(description = "작성자 ID", example = "123")
        private Long userId;

        @Schema(description = "좋아요 수", example = "15")
        private Integer likesCount;

        @Schema(description = "스포일러 포함 여부", example = "false")
        private Boolean isSpoiler;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private String contentType;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;
    }

    @Schema(description = "리뷰 검색 결과 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResponse {

        @Schema(description = "리뷰 ID", example = "1")
        private Long id;

        @Schema(description = "리뷰 내용", example = "스토리가 탄탄하고...")
        private String content;

        @Schema(description = "작성자명", example = "홍길동")
        private String username;

        @Schema(description = "좋아요 수", example = "15")
        private Integer likesCount;

        @Schema(description = "스포일러 포함 여부", example = "false")
        private Boolean isSpoiler;

        @Schema(description = "콘텐츠 타입", example = "MOVIE")
        private String contentType;

        @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
        private String contentTitle;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "하이라이트된 내용", example = "스토리가 <mark>탄탄하고</mark>...")
        private String highlightedContent;
    }

    @Schema(description = "리뷰 통계 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsResponse {

        @Schema(description = "총 리뷰 수", example = "1567")
        private Long totalReviews;

        @Schema(description = "영화 리뷰 수", example = "980")
        private Long movieReviews;

        @Schema(description = "드라마 리뷰 수", example = "587")
        private Long dramaReviews;

        @Schema(description = "스포일러 리뷰 수", example = "234")
        private Long spoilerReviews;

        @Schema(description = "평균 좋아요 수", example = "12.5")
        private Double averageLikes;

        @Schema(description = "총 좋아요 수", example = "19587")
        private Long totalLikes;
    }
}