package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewDTO {

    // 리뷰 생성 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        private String content;

        // 영화 ID 또는 드라마 ID 중 하나만 입력
        private Long movieId;
        private Long dramaId;
    }

    // 리뷰 수정 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        private String content;
    }

    // 리뷰 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String content;
        private String username;
        private Long userId;

        // 영화 정보 (있을 경우)
        private Long movieId;
        private String movieTitle;

        // 드라마 정보 (있을 경우)
        private Long dramaId;
        private String dramaTitle;

        private String createdAt;
        private String updatedAt;
    }

    // 리뷰 목록 조회용 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {

        private Long id;
        private String content;
        private String username;
        private String createdAt;

        // 간단한 작품 정보
        private String contentType; // "MOVIE" 또는 "DRAMA"
        private String contentTitle;
    }
}