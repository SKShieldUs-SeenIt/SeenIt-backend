// Mapper 클래스들도 수정
package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    // Entity -> Response DTO (상세)
    public MovieDTO.Response toResponse(Movie movie) {
        return MovieDTO.Response.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(genre -> MovieDTO.GenreInfo.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(movie.getReviews() != null ? movie.getReviews().size() : 0)
                .userAverageRating(calculateUserAverageRating(movie))
                .build();
    }

    // Entity -> ListResponse DTO (목록용)
    public MovieDTO.ListResponse toListResponse(Movie movie) {
        return MovieDTO.ListResponse.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .releaseDate(movie.getReleaseDate())
                .genreNames(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(movie.getReviews() != null ? movie.getReviews().size() : 0)
                .userAverageRating(calculateUserAverageRating(movie))
                .build();
    }

    // CreateRequest -> Entity
    public Movie toEntity(MovieDTO.CreateRequest request) {
        return Movie.builder()
                .tmdbId(request.getTmdbId())
                .title(request.getTitle())
                .overview(request.getOverview())
                .releaseDate(request.getReleaseDate())
                .posterPath(request.getPosterPath())
                .voteAverage(request.getVoteAverage())
                .voteCount(request.getVoteCount())
                .build();
        // 장르는 별도로 설정 필요
    }

    // 리스트 변환
    public List<MovieDTO.Response> toResponseList(List<Movie> movies) {
        return movies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MovieDTO.ListResponse> toListResponseList(List<Movie> movies) {
        return movies.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 사용자 평균 평점 계산
    private Double calculateUserAverageRating(Movie movie) {
        if (movie.getReviews() == null || movie.getReviews().isEmpty()) {
            return null;
        }

        // 실제로는 Rating 엔티티에서 계산해야 하지만,
        // 여기서는 임시로 TMDB 평점 반환
        return movie.getVoteAverage();
    }
}