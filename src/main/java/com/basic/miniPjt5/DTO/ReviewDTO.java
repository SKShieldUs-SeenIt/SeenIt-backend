package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class ReviewDTO {

    // 리뷰 생성 요청 DTO (기존 유지)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이하여야 합니다.")
        private String content;

        // 영화 ID 또는 드라마 ID 중 하나만 입력
        private Long movieId;
        private Long dramaId;

        // 별점도 함께 등록할 수 있도록 추가
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 10, message = "별점은 10점 이하여야 합니다.")
        private Integer rating; // 선택사항
    }

    // 리뷰 수정 요청 DTO (기존 유지)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이하여야 합니다.")
        private String content;
    }

    // 리뷰 응답 DTO (기존 확장)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String content;
        private String username;
        private Long userId;

        // 영화 정보 (있을 경우)
        private Long movieId;
        private String movieTitle;
        private String moviePosterPath;

        // 드라마 정보 (있을 경우)
        private Long dramaId;
        private String dramaTitle;
        private String dramaPosterPath;

        // 컨텐츠 타입 (MOVIE/DRAMA)
        private String contentType;

        // 해당 사용자가 이 컨텐츠에 준 별점
        private Integer userRating;

        // 좋아요 관련 (확장 가능)
        private Integer likeCount;
        private Boolean isLikedByCurrentUser;

        private String createdAt;
        private String updatedAt;
    }

    // 리뷰 목록 조회용 DTO (기존 확장)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        private Long id;
        @Size(max = 200) // 목록에서는 요약만
        private String content;
        private String username;
        private String createdAt;

        // 간단한 작품 정보
        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String contentTitle;
        private String contentPosterPath;

        // 사용자 별점
        private Integer userRating;
        private Integer likeCount;
    }

    // 컨텐츠별 리뷰 통계 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentReviewStats {
        private Long contentId;
        private String contentType;
        private String contentTitle;
        private Integer totalReviews;
        private Double averageRating;
        private Integer positiveReviews; // 7점 이상
        private Integer neutralReviews;  // 4-6점
        private Integer negativeReviews; // 3점 이하
    }
}