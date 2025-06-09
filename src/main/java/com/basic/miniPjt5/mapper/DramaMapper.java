package com.basic.miniPjt5.mapper;

import com.basic.miniPjt5.DTO.DramaDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.RatingRepository;
import com.basic.miniPjt5.repository.ReviewRepository;
import com.basic.miniPjt5.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DramaMapper {

    @Autowired
    private DramaRepository dramaRepository;

    @Autowired
    private RatingRepository ratingRepository; // 🆕 추가

    @Autowired
    private ReviewRepository reviewRepository; // 🆕 추가

    @Autowired
    private RatingService ratingService; // 🆕 추가

    // 🔥 수정된 Entity -> Response DTO (상세)
    public DramaDTO.Response toResponse(Drama drama) {
        // 🔥 Repository 기반으로 필요한 데이터 조회
        Long dramaId = drama.getId();
        Double userAverageRating = ratingRepository.findAverageScoreByDramaId(dramaId).orElse(null);
        Long reviewCount = reviewRepository.countByDramaId(dramaId);

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
                .combinedRating(drama.getCombinedRating()) // 이미 계산된 값 사용
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
                .reviewCount(reviewCount.intValue()) // 🔥 Repository 기반
                .userAverageRating(userAverageRating) // 🔥 Repository 기반
                .build();
    }

    // 🔥 수정된 Entity -> ListResponse DTO (목록용)
    public DramaDTO.ListResponse toListResponse(Drama drama) {
        // 🔥 Repository 기반으로 필요한 데이터 조회
        Long dramaId = drama.getId();
        Double userAverageRating = ratingRepository.findAverageScoreByDramaId(dramaId).orElse(null);
        Long reviewCount = reviewRepository.countByDramaId(dramaId);

        return DramaDTO.ListResponse.builder()
                .id(drama.getId())
                .tmdbId(drama.getTmdbId())
                .title(drama.getTitle())
                .posterPath(drama.getPosterPath())
                .voteAverage(drama.getVoteAverage())
                .voteCount(drama.getVoteCount())
                .combinedRating(drama.getCombinedRating()) // 이미 계산된 값 사용
                .firstAirDate(drama.getFirstAirDate())
                .numberOfSeasons(drama.getNumberOfSeasons())
                .numberOfEpisodes(drama.getNumberOfEpisodes())
                .genreNames(drama.getGenres() != null ?
                        drama.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .reviewCount(reviewCount.intValue()) // 🔥 Repository 기반
                .userAverageRating(userAverageRating) // 🔥 Repository 기반
                .build();
    }

    // 🔥 수정된 CreateRequest -> Entity
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
                .combinedRating(0.0) // 🔥 초기값 설정 (나중에 Service에서 계산)
                .build();

        // 🔥 매퍼에서는 저장하지 않음 (Service에서 처리)
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