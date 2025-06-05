package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.UserWatchedDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.UserWatched;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserWatchedMapper {

    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    // UserWatched -> Response DTO
    public UserWatchedDTO.Response toResponse(UserWatched userWatched) {
        UserWatchedDTO.Response.ResponseBuilder builder = UserWatchedDTO.Response.builder()
                .id(userWatched.getId())
                .contentType(userWatched.getContentType())
                .contentId(userWatched.getContentId())
                .watchedAt(userWatched.getWatchedAt());

        // 콘텐츠 타입에 따라 추가 정보 가져오기
        if (userWatched.getContentType() == UserWatched.ContentType.MOVIE) {
            Optional<Movie> movieOpt = movieRepository.findById(userWatched.getContentId());
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                builder.contentTitle(movie.getTitle())
                       .posterPath(movie.getPosterPath())
                       .releaseDate(movie.getReleaseDate())
                       .voteAverage(movie.getVoteAverage())
                       .combinedRating(movie.getCombinedRating())
                       .genreNames(movie.getGenres() != null ? 
                           movie.getGenres().stream()
                               .map(genre -> genre.getName())
                               .collect(Collectors.toList()) : 
                           Collections.emptyList());
            } else {
                builder.contentTitle("알 수 없는 영화");
            }
        } else if (userWatched.getContentType() == UserWatched.ContentType.DRAMA) {
            Optional<Drama> dramaOpt = dramaRepository.findById(userWatched.getContentId());
            if (dramaOpt.isPresent()) {
                Drama drama = dramaOpt.get();
                builder.contentTitle(drama.getTitle())
                       .posterPath(drama.getPosterPath())
                       .releaseDate(drama.getFirstAirDate())
                       .voteAverage(drama.getVoteAverage())
                       .combinedRating(drama.getCombinedRating())
                       .numberOfSeasons(drama.getNumberOfSeasons())
                       .numberOfEpisodes(drama.getNumberOfEpisodes())
                       .genreNames(drama.getGenres() != null ? 
                           drama.getGenres().stream()
                               .map(genre -> genre.getName())
                               .collect(Collectors.toList()) : 
                           Collections.emptyList());
            } else {
                builder.contentTitle("알 수 없는 드라마");
            }
        }

        return builder.build();
    }

    // UserWatched -> ListResponse DTO (목록용, 간소화)
    public UserWatchedDTO.ListResponse toListResponse(UserWatched userWatched) {
        UserWatchedDTO.ListResponse.ListResponseBuilder builder = UserWatchedDTO.ListResponse.builder()
                .id(userWatched.getId())
                .contentType(userWatched.getContentType())
                .contentId(userWatched.getContentId())
                .watchedAt(userWatched.getWatchedAt());

        String primaryGenre = null;

        if (userWatched.getContentType() == UserWatched.ContentType.MOVIE) {
            Optional<Movie> movieOpt = movieRepository.findById(userWatched.getContentId());
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                builder.contentTitle(movie.getTitle())
                       .posterPath(movie.getPosterPath())
                       .voteAverage(movie.getVoteAverage())
                       .combinedRating(movie.getCombinedRating());
                
                // 첫 번째 장르만 가져오기
                if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                    primaryGenre = movie.getGenres().get(0).getName();
                }
            } else {
                builder.contentTitle("알 수 없는 영화");
            }
        } else if (userWatched.getContentType() == UserWatched.ContentType.DRAMA) {
            Optional<Drama> dramaOpt = dramaRepository.findById(userWatched.getContentId());
            if (dramaOpt.isPresent()) {
                Drama drama = dramaOpt.get();
                builder.contentTitle(drama.getTitle())
                       .posterPath(drama.getPosterPath())
                       .voteAverage(drama.getVoteAverage())
                       .combinedRating(drama.getCombinedRating());
                
                // 첫 번째 장르만 가져오기
                if (drama.getGenres() != null && !drama.getGenres().isEmpty()) {
                    primaryGenre = drama.getGenres().get(0).getName();
                }
            } else {
                builder.contentTitle("알 수 없는 드라마");
            }
        }

        return builder.primaryGenre(primaryGenre).build();
    }

    // 리스트 변환
    public List<UserWatchedDTO.Response> toResponseList(List<UserWatched> userWatchedList) {
        return userWatchedList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserWatchedDTO.ListResponse> toListResponseList(List<UserWatched> userWatchedList) {
        return userWatchedList.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }
}