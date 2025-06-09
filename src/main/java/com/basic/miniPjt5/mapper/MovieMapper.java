// Mapper 클래스들도 수정
package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.MovieDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.repository.RatingRepository;
import com.basic.miniPjt5.repository.ReviewRepository;
import com.basic.miniPjt5.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository; // 🆕 추가

    @Autowired
    private ReviewRepository reviewRepository; // 🆕 추가

    @Autowired
    private RatingService ratingService; // 🆕 추가

    // 🔥 수정된 Entity -> Response DTO (상세)
    public MovieDTO.Response toResponse(Movie movie) {
        // 🔥 Repository 기반으로 필요한 데이터 조회
        Long movieId = movie.getId();
        Double userAverageRating = ratingRepository.findAverageScoreByMovieId(movieId).orElse(null);
        Long reviewCount = reviewRepository.countByMovieId(movieId);

        return MovieDTO.Response.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .combinedRating(movie.getCombinedRating()) // 이미 계산된 값 사용
                .genres(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(genre -> MovieDTO.GenreInfo.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(reviewCount.intValue()) // 🔥 Repository 기반
                .userAverageRating(userAverageRating) // 🔥 Repository 기반
                .build();
    }

    // 🔥 수정된 Entity -> ListResponse DTO (목록용)
    public MovieDTO.ListResponse toListResponse(Movie movie) {
        // 🔥 Repository 기반으로 필요한 데이터 조회
        Long movieId = movie.getId();
        Double userAverageRating = ratingRepository.findAverageScoreByMovieId(movieId).orElse(null);
        Long reviewCount = reviewRepository.countByMovieId(movieId);

        return MovieDTO.ListResponse.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .combinedRating(movie.getCombinedRating()) // 이미 계산된 값 사용
                .releaseDate(movie.getReleaseDate())
                .genreNames(movie.getGenres() != null ?
                        movie.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(reviewCount.intValue()) // 🔥 Repository 기반
                .userAverageRating(userAverageRating) // 🔥 Repository 기반
                .build();
    }

    // 🔥 수정된 CreateRequest -> Entity
    public Movie toEntity(MovieDTO.CreateRequest request) {
        Movie movie = Movie.builder()
                .tmdbId(request.getTmdbId())
                .title(request.getTitle())
                .overview(request.getOverview())
                .releaseDate(request.getReleaseDate())
                .posterPath(request.getPosterPath())
                .voteAverage(request.getVoteAverage())
                .voteCount(request.getVoteCount())
                .combinedRating(0.0) // 🔥 초기값 설정 (나중에 Service에서 계산)
                .build();

        // 🔥 매퍼에서는 저장하지 않음 (Service에서 처리)
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