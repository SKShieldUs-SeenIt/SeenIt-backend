// Mapper 클래스들도 수정
package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    @Autowired
    private MovieRepository movieRepository;

    // Entity -> Response DTO (상세)
    public MovieDTO.Response toResponse(Movie movie) {
        // combinedRating 업데이트 (매번 최신 값으로)
        movie.updateCombinedRating();
        movieRepository.save(movie);

        return MovieDTO.Response.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .combinedRating(movie.getCombinedRating()) // ✅ 추가된 매핑
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(genre -> MovieDTO.GenreInfo.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(movie.getReviews() != null ? movie.getReviews().size() : 0)
                .userAverageRating(movie.getUserAverageRating()) // ✅ 수정된 부분
                .build();
    }

    // Entity -> ListResponse DTO (목록용)
    public MovieDTO.ListResponse toListResponse(Movie movie) {
        // combinedRating 업데이트 (매번 최신 값으로)
        movie.updateCombinedRating();
        movieRepository.save(movie);

        return MovieDTO.ListResponse.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .combinedRating(movie.getCombinedRating()) // ✅ 추가된 매핑
                .releaseDate(movie.getReleaseDate())
                .genreNames(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(movie.getReviews() != null ? movie.getReviews().size() : 0)
                .userAverageRating(movie.getUserAverageRating()) // ✅ 수정된 부분
                .build();
    }

    // CreateRequest -> Entity
    public Movie toEntity(MovieDTO.CreateRequest request) {
        Movie movie = Movie.builder()
                .tmdbId(request.getTmdbId())
                .title(request.getTitle())
                .overview(request.getOverview())
                .releaseDate(request.getReleaseDate())
                .posterPath(request.getPosterPath())
                .voteAverage(request.getVoteAverage())
                .voteCount(request.getVoteCount())
                .build();

        // 생성 시 combinedRating 초기화
        movie.updateCombinedRating();
        movieRepository.save(movie);

        return movie;
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
}