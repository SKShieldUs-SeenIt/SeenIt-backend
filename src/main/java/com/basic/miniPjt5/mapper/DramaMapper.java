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
        // combinedRating 업데이트 (매번 최신 값으로)
        drama.updateCombinedRating();

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
                .combinedRating(drama.getCombinedRating()) // ✅ 추가된 매핑
                .numberOfSeasons(drama.getNumberOfSeasons())
                .numberOfEpisodes(drama.getNumberOfEpisodes())
                .genres(drama.getGenres() != null ?
                        drama.getGenres().stream()
                                .map(genre -> DramaDTO.GenreInfo.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(drama.getReviews() != null ? drama.getReviews().size() : 0)
                .userAverageRating(drama.getUserAverageRating()) // ✅ 수정된 부분
                .build();
    }

    // Entity -> ListResponse DTO (목록용)
    public DramaDTO.ListResponse toListResponse(Drama drama) {
        // combinedRating 업데이트 (매번 최신 값으로)
        drama.updateCombinedRating();

        return DramaDTO.ListResponse.builder()
                .id(drama.getId())
                .tmdbId(drama.getTmdbId())
                .title(drama.getTitle())
                .posterPath(drama.getPosterPath())
                .voteAverage(drama.getVoteAverage())
                .voteCount(drama.getVoteCount())
                .combinedRating(drama.getCombinedRating()) // ✅ 추가된 매핑
                .firstAirDate(drama.getFirstAirDate())
                .numberOfSeasons(drama.getNumberOfSeasons())
                .numberOfEpisodes(drama.getNumberOfEpisodes())
                .genreNames(drama.getGenres() != null ?
                        drama.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(drama.getReviews() != null ? drama.getReviews().size() : 0)
                .userAverageRating(drama.getUserAverageRating()) // ✅ 수정된 부분
                .build();
    }

    // CreateRequest -> Entity
    public Drama toEntity(DramaDTO.CreateRequest request) {
        Drama drama = Drama.builder()
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

        // 생성 시 combinedRating 초기화
        drama.updateCombinedRating();

        return drama;
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
}