package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class MovieDTO {

    @Schema(description = "영화 생성 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @Schema(description = "TMDB ID", example = "76600", required = true)
        @NotNull(message = "TMDB ID는 필수입니다.")
        private Long tmdbId;

        @Schema(description = "영화 제목", example = "아바타: 물의 길", required = true)
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;

        @Schema(description = "영화 줄거리", example = "판도라 행성에서 벌어지는 새로운 모험...")
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;

        @Schema(description = "개봉일", example = "2022-12-14")
        private String releaseDate;

        @Schema(description = "포스터 경로", example = "/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.5", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "1234", minimum = "0")
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;

        @Schema(description = "장르 ID 목록", example = "[28, 12, 878]")
        private List<Long> genreIds;
    }

    @Schema(description = "영화 수정 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
        private String title;

        @Schema(description = "영화 줄거리", example = "판도라 행성에서...")
        @Size(max = 2000, message = "줄거리는 2000자를 초과할 수 없습니다.")
        private String overview;

        @Schema(description = "개봉일", example = "2022-12-14")
        private String releaseDate;

        @Schema(description = "포스터 경로", example = "/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.5", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "평점은 10.0 이하여야 합니다.")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "1234", minimum = "0")
        @Min(value = 0, message = "투표 수는 0 이상이어야 합니다.")
        private Integer voteCount;

        @Schema(description = "장르 ID 목록", example = "[28, 12]")
        private List<Long> genreIds;
    }

    @Schema(description = "영화 상세 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "영화 ID", example = "1")
        private Long id;

        @Schema(description = "TMDB ID", example = "76600")
        private Long tmdbId;

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        private String title;

        @Schema(description = "영화 줄거리", example = "판도라 행성에서...")
        private String overview;

        @Schema(description = "개봉일", example = "2022-12-14")
        private String releaseDate;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.5")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "1234")
        private Integer voteCount;

        @Schema(description = "장르 목록")
        private List<GenreInfo> genres;

        @Schema(description = "리뷰 목록")
        private List<ReviewInfo> reviews;

        @Schema(description = "리뷰 개수", example = "25")
        private Integer reviewCount;

        @Schema(description = "사용자 평균 평점", example = "8.7")
        private Double userAverageRating;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }

    @Schema(description = "영화 목록 응답 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {

        @Schema(description = "영화 ID", example = "1")
        private Long id;

        @Schema(description = "TMDB ID", example = "76600")
        private Long tmdbId;

        @Schema(description = "영화 제목", example = "아바타: 물의 길")
        private String title;

        @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
        private String posterPath;

        @Schema(description = "평균 평점", example = "8.5")
        private Double voteAverage;

        @Schema(description = "투표 수", example = "1234")
        private Integer voteCount;

        @Schema(description = "개봉일", example = "2022-12-14")
        private String releaseDate;

        @Schema(description = "장르명 목록", example = "[\"액션\", \"모험\", \"SF\"]")
        private List<String> genreNames;

        @Schema(description = "리뷰 개수", example = "25")
        private Integer reviewCount;

        @Schema(description = "사용자 평균 평점", example = "8.7")
        private Double userAverageRating;
    }

    @Schema(description = "영화 검색 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {

        @Schema(description = "검색할 제목", example = "아바타")
        private String title;

        @Schema(description = "장르 ID 목록", example = "[28, 12]")
        private List<Long> genreIds;

        @Schema(description = "개봉년도", example = "2022")
        private String releaseYear;

        @Schema(description = "최소 평점", example = "7.0", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "최소 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최소 평점은 10.0 이하여야 합니다.")
        private Double minRating;

        @Schema(description = "최대 평점", example = "10.0", minimum = "0.0", maximum = "10.0")
        @DecimalMin(value = "0.0", message = "최대 평점은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "최대 평점은 10.0 이하여야 합니다.")
        private Double maxRating;

        @Schema(description = "정렬 기준", example = "popularity", allowableValues = {"title", "rating", "release_date", "popularity"})
        @Pattern(regexp = "title|rating|release_date|popularity",
                message = "정렬 기준은 title, rating, release_date, popularity 중 하나여야 합니다.")
        private String sortBy = "popularity";

        @Schema(description = "정렬 방향", example = "desc", allowableValues = {"asc", "desc"})
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        private String sortDirection = "desc";
    }

    @Schema(description = "장르 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenreInfo {

        @Schema(description = "장르 ID", example = "28")
        private Long id;

        @Schema(description = "장르명", example = "액션")
        private String name;
    }

    @Schema(description = "리뷰 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewInfo {

        @Schema(description = "리뷰 ID", example = "1")
        private Long id;

        @Schema(description = "리뷰 내용", example = "정말 재미있는 영화였어요!")
        private String content;

        @Schema(description = "사용자명", example = "홍길동")
        private String username;

        @Schema(description = "사용자 ID", example = "123")
        private Long userId;

        @Schema(description = "생성일시", example = "2024-06-04T15:30:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-06-04T16:00:00")
        private String updatedAt;
    }
}