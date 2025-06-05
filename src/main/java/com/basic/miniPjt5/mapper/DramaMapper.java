package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DramaMapper {

    // Entity -> Response DTO (상세)
    public DramaDTO.Response toResponse(Drama drama) {
        return DramaDTO.Response.builder()
                .id(drama.getId())
                .tmdbId(drama.getTmdbId())
                .title(drama.getTitle())
                .overview(drama.getOverview())
                .firstAirDate(drama.getFirstAirDate())
                .lastAirDate(drama.getLastAirDate())
                .posterPath(drama.getPosterPath())
                .voteAverage(drama.getVoteAverage())
                .voteCount(drama.getVoteCount())
                .numberOfSeasons(drama.getNumberOfSeasons())
                .numberOfEpisodes(drama.getNumberOfEpisodes())
                .genres(drama.getGenres() != null ?drama.getGenres().stream()
                            .map(genre -> DramaDTO.GenreInfo.builder()
                                .id(genre.getId())
                                .name(genre.getName())
                                .build())
                            .collect(Collectors.toList()) :
                            Collections.emptyList())
                .reviewCount(drama.getReviews() != null ? drama.getReviews().size() : 0)
                .userAverageRating(calculateUserAverageRating(drama))
                .build();
    }

    // Entity -> ListResponse DTO (목록용)
    public DramaDTO.ListResponse toListResponse(Drama drama) {
        return DramaDTO.ListResponse.builder()
                .id(drama.getId())
                .tmdbId(drama.getTmdbId())
                .title(drama.getTitle())
                .posterPath(drama.getPosterPath())
                .voteAverage(drama.getVoteAverage())
                .voteCount(drama.getVoteCount())
                .firstAirDate(drama.getFirstAirDate())
                .numberOfSeasons(drama.getNumberOfSeasons())
                .numberOfEpisodes(drama.getNumberOfEpisodes())
                .genreNames(drama.getGenres() != null ?
                        drama.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(drama.getReviews() != null ? drama.getReviews().size() : 0)
                .userAverageRating(calculateUserAverageRating(drama))
                .build();
    }

    // CreateRequest -> Entity
    public Drama toEntity(DramaDTO.CreateRequest request) {
        return Drama.builder()
                .tmdbId(request.getTmdbId())
                .title(request.getTitle())
                .overview(request.getOverview())
                .firstAirDate(request.getFirstAirDate())
                .lastAirDate(request.getLastAirDate())
                .posterPath(request.getPosterPath())
                .voteAverage(request.getVoteAverage())
                .voteCount(request.getVoteCount())
                .numberOfSeasons(request.getNumberOfSeasons())
                .numberOfEpisodes(request.getNumberOfEpisodes())
                .build();
        // 장르는 별도로 설정 필요
    }

    // 리스트 변환
    public List<DramaDTO.Response> toResponseList(List<Drama> dramas) {
        return dramas.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DramaDTO.ListResponse> toListResponseList(List<Drama> dramas) {
        return dramas.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 사용자 평균 평점 계산
    private Double calculateUserAverageRating(Drama drama) {
        if (drama.getReviews() == null || drama.getReviews().isEmpty()) {
            return null;
        }

        // 실제로는 Rating 엔티티에서 계산해야 하지만,
        // 여기서는 임시로 TMDB 평점 반환
        return drama.getVoteAverage();
    }
}