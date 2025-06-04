package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingStatisticsService {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    /**
     * 영화 평점 통계 상세 조회
     */
    public RatingDTO.StatisticsResponse getMovieStatistics(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        Double averageScore = ratingRepository.findAverageScoreByMovieId(movieId).orElse(0.0);
        Long totalCount = ratingRepository.countByMovieId(movieId);
        Map<Integer, Long> distribution = getScoreDistribution(movieId, null);

        return new RatingDTO.StatisticsResponse(
                movieId,                        // contentId
                "MOVIE",                        // contentType
                movie.getTitle(),               // contentTitle
                movie.getPosterPath(),          // posterPath (필요시 null로 변경)
                roundToTwoDecimals(averageScore), // averageScore
                totalCount,                     // totalRatingCount
                distribution,                   // scoreDistribution
                null,                          // standardDeviation
                null,                          // tmdbRating
                null,                          // tmdbVoteCount
                null,                          // highestScore
                null,                          // lowestScore
                null                           // recentTrends
        );
    }

    /**
     * 드라마 평점 통계 상세 조회
     */
    public RatingDTO.StatisticsResponse getDramaStatistics(Long dramaId) {
        Drama drama = dramaRepository.findById(dramaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        Double averageScore = ratingRepository.findAverageScoreByDramaId(dramaId).orElse(0.0);
        Long totalCount = ratingRepository.countByDramaId(dramaId);
        Map<Integer, Long> distribution = getScoreDistribution(null, dramaId);

        return new RatingDTO.StatisticsResponse(
                dramaId,                        // contentId
                "DRAMA",                        // contentType
                drama.getTitle(),               // contentTitle
                drama.getPosterPath(),          // posterPath (필요시 null로 변경)
                roundToTwoDecimals(averageScore), // averageScore
                totalCount,                     // totalRatingCount
                distribution,                   // scoreDistribution
                null,                          // standardDeviation
                null,                          // tmdbRating
                null,                          // tmdbVoteCount
                null,                          // highestScore
                null,                          // lowestScore
                null                           // recentTrends
        );
    }

    /**
     * 평점 높은 영화 목록 조회
     */
    public Page<RatingDTO.SimpleRating> getTopRatedMovies(Pageable pageable) {
        return ratingRepository.findTopRatedMovies(pageable)
                .map(this::convertToSimpleRating);
    }

    /**
     * 평점 높은 드라마 목록 조회
     */
    public Page<RatingDTO.SimpleRating> getTopRatedDramas(Pageable pageable) {
        return ratingRepository.findTopRatedDramas(pageable)
                .map(this::convertToSimpleRating);
    }

    /**
     * 최근 평점이 많이 등록된 작품들 조회
     */
    public List<RatingDTO.SimpleRating> getRecentlyPopularContents(int limit) {
        List<Object[]> recentMovies = ratingRepository.findRecentlyPopularMovies(limit / 2);
        List<Object[]> recentDramas = ratingRepository.findRecentlyPopularDramas(limit / 2);

        List<RatingDTO.SimpleRating> results = new ArrayList<>();
        
        // 영화 변환
        recentMovies.forEach(result -> {
            Long movieId = (Long) result[0];
            String title = (String) result[1];
            String posterPath = (String) result[2];
            Double avgScore = (Double) result[3];
            Long count = ((Number) result[4]).longValue();
            
            results.add(new RatingDTO.SimpleRating(
                    movieId, "MOVIE", title, roundToTwoDecimals(avgScore), count, posterPath, null
            ));
        });
        
        // 드라마 변환
        recentDramas.forEach(result -> {
            Long dramaId = (Long) result[0];
            String title = (String) result[1];
            String posterPath = (String) result[2];
            Double avgScore = (Double) result[3];
            Long count = ((Number) result[4]).longValue();
            
            results.add(new RatingDTO.SimpleRating(
                    dramaId, "DRAMA", title, roundToTwoDecimals(avgScore), count, posterPath, null
            ));
        });

        // 평점 개수 순으로 정렬하여 반환
        return results.stream()
                .sorted((a, b) -> Long.compare(b.getRatingCount(), a.getRatingCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 장르별 평균 평점 조회
     */
    public List<Map<String, Object>> getAverageRatingByGenre() {
        List<Object[]> movieGenreStats = ratingRepository.findAverageRatingByMovieGenre();
        List<Object[]> dramaGenreStats = ratingRepository.findAverageRatingByDramaGenre();

        List<Map<String, Object>> results = new ArrayList<>();
        
        // 영화 장르별 통계
        movieGenreStats.forEach(stat -> {
            Map<String, Object> genreData = new HashMap<>();
            genreData.put("genreName", stat[0]);
            genreData.put("contentType", "MOVIE");
            genreData.put("averageScore", roundToTwoDecimals((Double) stat[1]));
            genreData.put("contentCount", stat[2]);
            results.add(genreData);
        });
        
        // 드라마 장르별 통계
        dramaGenreStats.forEach(stat -> {
            Map<String, Object> genreData = new HashMap<>();
            genreData.put("genreName", stat[0]);
            genreData.put("contentType", "DRAMA");
            genreData.put("averageScore", roundToTwoDecimals((Double) stat[1]));
            genreData.put("contentCount", stat[2]);
            results.add(genreData);
        });

        return results;
    }

    /**
     * 개인 평점 통계 조회
     */
    public Map<String, Object> getUserRatingStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 사용자가 준 총 평점 개수
        Long totalRatings = ratingRepository.countByUserId(userId);
        
        // 사용자 평균 평점
        Double userAverageScore = ratingRepository.findAverageScoreByUserId(userId).orElse(0.0);
        
        // 사용자 평점 분포
        Object[][] userDistribution = ratingRepository.findScoreDistributionByUserId(userId);
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            distribution.put(i, 0L);
        }
        for (Object[] row : userDistribution) {
            distribution.put((Integer) row[0], ((Number) row[1]).longValue());
        }
        
        // 최고점을 준 영화/드라마 (10점)
        List<Object[]> topRatedContents = ratingRepository.findUserTopRatedContents(userId);
        
        stats.put("totalRatings", totalRatings);
        stats.put("averageScore", roundToTwoDecimals(userAverageScore));
        stats.put("scoreDistribution", distribution);
        stats.put("topRatedContents", topRatedContents);
        
        return stats;
    }

    // 헬퍼 메서드들
    private Map<Integer, Long> getScoreDistribution(Long movieId, Long dramaId) {
        Object[][] distribution = null;
        
        if (movieId != null) {
            distribution = ratingRepository.findScoreDistributionByMovieId(movieId);
        } else if (dramaId != null) {
            distribution = ratingRepository.findScoreDistributionByDramaId(dramaId);
        }
        
        Map<Integer, Long> result = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            result.put(i, 0L);
        }
        
        if (distribution != null) {
            for (Object[] row : distribution) {
                result.put((Integer) row[0], ((Number) row[1]).longValue());
            }
        }
        
        return result;
    }

    private RatingDTO.SimpleRating convertToSimpleRating(Object[] result) {
        Long id = (Long) result[0];
        String type = (String) result[1];
        String title = (String) result[2];
        String posterPath = (String) result[3];
        Double avgScore = (Double) result[4];
        Long count = ((Number) result[5]).longValue();
        
        return new RatingDTO.SimpleRating(
                id, type, title, roundToTwoDecimals(avgScore), count, posterPath, null
        );
    }

    private Double roundToTwoDecimals(Double value) {
        if (value == null) return 0.0;
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}