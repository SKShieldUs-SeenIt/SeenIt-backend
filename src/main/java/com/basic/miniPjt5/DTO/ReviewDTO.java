package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class ReviewDTO {

    // 리뷰 생성 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 2, max = 200, message = "제목은 2-200자 사이여야 합니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "내용은 10-2000자 사이여야 합니다.")
        private String content;

        // 영화 ID 또는 드라마 ID 중 하나만 입력
        private Long movieId;
        private Long dramaId;

        @Builder.Default
        private Boolean isSpoiler = false;
    }

    // 리뷰 수정 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 2, max = 200, message = "제목은 2-200자 사이여야 합니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "내용은 10-2000자 사이여야 합니다.")
        private String content;

        @Builder.Default
        private Boolean isSpoiler = false;
    }

    // 리뷰 상세 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String title;
        private String content;
        private String username;
        private Long userId;
        private Integer likesCount;
        private Boolean isSpoiler;

        // 영화 정보 (있을 경우)
        private Long movieId;
        private String movieTitle;
        private String moviePosterPath;

        // 드라마 정보 (있을 경우)
        private Long dramaId;
        private String dramaTitle;
        private String dramaPosterPath;

        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String createdAt;
        private String updatedAt;
    }

    // 리뷰 목록용 간단한 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        private Long id;
        private String title;
        private String content; // 요약된 내용 (100자 제한 등)
        private String username;
        private Long userId;
        private Integer likesCount;
        private Boolean isSpoiler;
        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String contentTitle; // 영화/드라마 제목
        private String createdAt;
    }

    // 리뷰 검색 결과 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResponse {

        private Long id;
        private String title;
        private String content;
        private String username;
        private Integer likesCount;
        private Boolean isSpoiler;
        private String contentType;
        private String contentTitle;
        private String posterPath;
        private String createdAt;
        private String highlightedTitle; // 검색어 하이라이트된 제목
        private String highlightedContent; // 검색어 하이라이트된 내용
    }

    // 리뷰 통계 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsResponse {

        private Long totalReviews;
        private Long movieReviews;
        private Long dramaReviews;
        private Long spoilerReviews;
        private Double averageLikes;
        private Long totalLikes;
    }
}