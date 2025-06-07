package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.Rating;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.repository.RatingRepository;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    // 별점 생성 또는 수정
    @Transactional
    public RatingDTO.Response createOrUpdateRating(Long userId, RatingDTO.Request requestDto) {
        // 입력 검증
        validateRatingRequest(requestDto);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED, "정상 상태의 사용자만 별점을 등록/수정할 수 있습니다.");
        }

        Rating rating = null;

        if (requestDto.getMovieId() != null) {
            // 영화 별점 처리
            Movie movie = movieRepository.findById(requestDto.getMovieId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

            Optional<Rating> existingRating = ratingRepository.findByUserIdAndMovieId(userId, requestDto.getMovieId());

            if (existingRating.isPresent()) {
                // 기존 별점 수정
                rating = existingRating.get();
                rating.updateScore(requestDto.getScore());
            } else {
                // 새 별점 생성
                rating = new Rating(user, requestDto.getScore(), movie);
                rating = ratingRepository.save(rating);
            }
            Double newRating = calculateMovieCombinedRating(requestDto.getMovieId());
            movie.setCombinedRating(newRating);
            movieRepository.save(movie);
        } else {
            // 드라마 별점 처리
            Drama drama = dramaRepository.findById(requestDto.getDramaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

            Optional<Rating> existingRating = ratingRepository.findByUserIdAndDramaId(userId, requestDto.getDramaId());

            if (existingRating.isPresent()) {
                // 기존 별점 수정
                rating = existingRating.get();
                rating.updateScore(requestDto.getScore());
            } else {
                // 새 별점 생성
                rating = new Rating(user, requestDto.getScore(), drama);
                rating = ratingRepository.save(rating);
            }
            Double newRating = calculateDramaCombinedRating(requestDto.getDramaId());
            drama.setCombinedRating(newRating);
            dramaRepository.save(drama);
        }

        return convertToResponseDto(rating);
    }

    // 별점 삭제
    @Transactional
    public void deleteRating(Long userId, Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RATING_NOT_FOUND));

        // 작성자 확인
        if (!rating.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.RATING_ACCESS_DENIED);
        }

        if (rating.getUser().getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED, "정상 상태의 사용자만 별점을 삭제할 수 있습니다.");
        }


        Long movieId = rating.getMovie() != null ? rating.getMovie().getId() : null;
        Long dramaId = rating.getDrama() != null ? rating.getDrama().getId() : null;

        ratingRepository.deleteById(ratingId);
        ratingRepository.flush();

        // ⭐ 평점 재계산
        if (movieId != null) {
            Double newRating = calculateMovieCombinedRating(movieId);
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));
            movie.setCombinedRating(newRating);
            movieRepository.save(movie);
        }
        if (dramaId != null) {
            Double newRating = calculateDramaCombinedRating(dramaId);
            Drama drama = dramaRepository.findById(dramaId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));
            drama.setCombinedRating(newRating);
            dramaRepository.save(drama);
        }

    }

    // 사용자가 준 별점 조회
    public RatingDTO.Response getUserRating(Long userId, Long movieId, Long dramaId) {
        Rating rating = null;

        if (movieId != null) {
            rating = ratingRepository.findByUserIdAndMovieId(userId, movieId).orElse(null);
        } else if (dramaId != null) {
            rating = ratingRepository.findByUserIdAndDramaId(userId, dramaId).orElse(null);
        }

        return rating != null ? convertToResponseDto(rating) : null;
    }

    // 영화 평균 별점 조회
    public RatingDTO.AverageResponse getMovieAverageRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // 사용자 평점만
        Double combinedRating = movie.getCombinedRating() != null ?
                movie.getCombinedRating() : movie.getVoteAverage();
        Long userRatingCount = ratingRepository.countByMovieId(movieId);

        Long totalRatingCount = (long) movie.getVoteCount() + userRatingCount;

        return RatingDTO.AverageResponse.builder()
                .contentId(movieId)
                .contentType("MOVIE")
                .contentTitle(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .averageScore(roundToTwoDecimals(combinedRating))  // ⭐ 통합 평점 사용
                .ratingCount(totalRatingCount)  // ⭐ 전체 투표 수
                .tmdbRating(movie.getVoteAverage())
                .build();

    }

    // 드라마 평균 별점 조회
    public RatingDTO.AverageResponse getDramaAverageRating(Long dramaId) {
        Drama drama = dramaRepository.findById(dramaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        Double combinedRating = drama.getCombinedRating() != null ?
                drama.getCombinedRating() : drama.getVoteAverage();

        // 사용자 평점 통계
        Long userRatingCount = ratingRepository.countByDramaId(dramaId);

        // 전체 투표 수 = TMDB 투표 수 + 사용자 투표 수
        Long totalRatingCount = (long) drama.getVoteCount() + userRatingCount;

        return RatingDTO.AverageResponse.builder()
                .contentId(dramaId)
                .contentType("DRAMA")
                .contentTitle(drama.getTitle())
                .posterPath(drama.getPosterPath())
                .averageScore(roundToTwoDecimals(combinedRating))  // ⭐ 통합 평점 사용
                .ratingCount(totalRatingCount)  // ⭐ 전체 투표 수
                .tmdbRating(drama.getVoteAverage())
                .build();
    }

    // 영화별 별점 목록 조회
    public Page<RatingDTO.Response> getMovieRatings(Long movieId, Pageable pageable) {
        // 영화 존재 확인
        if (!movieRepository.existsById(movieId)) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }

        Page<Rating> ratings = ratingRepository.findByMovieIdOrderByCreatedAtDesc(movieId, pageable);
        return ratings.map(this::convertToResponseDto);
    }

    // 드라마별 별점 목록 조회
    public Page<RatingDTO.Response> getDramaRatings(Long dramaId, Pageable pageable) {
        // 드라마 존재 확인
        if (!dramaRepository.existsById(dramaId)) {
            throw new BusinessException(ErrorCode.DRAMA_NOT_FOUND);
        }

        Page<Rating> ratings = ratingRepository.findByDramaIdOrderByCreatedAtDesc(dramaId, pageable);
        return ratings.map(this::convertToResponseDto);
    }

    // 사용자별 별점 목록 조회
    public Page<RatingDTO.Response> getUserRatings(Long userId, Pageable pageable) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Page<Rating> ratings = ratingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return ratings.map(this::convertToResponseDto);
    }

    // 별점 분포 조회
    public Map<String, Long> getScoreDistribution(Long movieId, Long dramaId) {
        Object[][] distribution = null;

        if (movieId != null) {
            if (!movieRepository.existsById(movieId)) {
                throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
            }
            distribution = ratingRepository.findScoreDistributionByMovieId(movieId);
        } else if (dramaId != null) {
            if (!dramaRepository.existsById(dramaId)) {
                throw new BusinessException(ErrorCode.DRAMA_NOT_FOUND);
            }
            distribution = ratingRepository.findScoreDistributionByDramaId(dramaId);
        }

        Map<String, Long> result = new HashMap<>();

        // 0.5~5.0점 초기화 (0.5 단위)
        for (int i = 1; i <= 10; i++) {
            BigDecimal score = new BigDecimal(i).divide(new BigDecimal("2"));
            result.put(score.toPlainString(), 0L);
        }

        // 실제 데이터 입력
        if (distribution != null) {
            for (Object[] row : distribution) {
                if (row[0] != null && row[1] != null) {
                    BigDecimal score;
                    if (row[0] instanceof BigDecimal) {
                        score = (BigDecimal) row[0];
                    } else {
                        score = new BigDecimal(row[0].toString());
                    }
                    Long count = ((Number) row[1]).longValue();
                    result.put(score.toPlainString(), count);
                }
            }
        }

        return result;
    }
    // 요청 검증
    private void validateRatingRequest(RatingDTO.Request requestDto) {
        if ((requestDto.getMovieId() == null && requestDto.getDramaId() == null) ||
                (requestDto.getMovieId() != null && requestDto.getDramaId() != null)) {
            throw new BusinessException(ErrorCode.CONTENT_TYPE_INVALID);
        }
    }

    // DTO 변환
    private RatingDTO.Response convertToResponseDto(Rating rating) {
        RatingDTO.Response dto = new RatingDTO.Response();
        dto.setId(rating.getId());
        dto.setScore(rating.getScore());
        dto.setUsername(rating.getUser().getName());
        dto.setUserId(rating.getUser().getUserId());

        if (rating.getMovie() != null) {
            dto.setMovieId(rating.getMovie().getId());
            dto.setTmdbId(rating.getMovie().getTmdbId());
            dto.setMovieTitle(rating.getMovie().getTitle());
            dto.setMoviePosterPath(rating.getMovie().getPosterPath());
            dto.setContentType("MOVIE");
        }

        if (rating.getDrama() != null) {
            dto.setDramaId(rating.getDrama().getId());
            dto.setDramaTitle(rating.getDrama().getTitle());
            dto.setDramaPosterPath(rating.getDrama().getPosterPath());
            dto.setContentType("DRAMA");
        }

        dto.setCreatedAt(rating.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setUpdatedAt(rating.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return dto;
    }

    // 소수점 둘째 자리까지 반올림하는 헬퍼 메서드
    private Double roundToTwoDecimals(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }

    public Double calculateMovieCombinedRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getVoteAverage() == null || movie.getVoteCount() == null) {
            return 0.0;
        }

        // TMDB 총점
        double tmdbTotalScore = movie.getVoteAverage() / 2 * movie.getVoteCount();

        // 🔥 Repository 쿼리로 사용자 평점 계산 (컬렉션 참조 X)
        Double userAverageScore = ratingRepository.findAverageScoreByMovieId(movieId).orElse(0.0);
        Long userRatingCount = ratingRepository.countByMovieId(movieId);

        // 전체 투표 수
        int totalVotes = movie.getVoteCount() + userRatingCount.intValue();

        if (totalVotes == 0) return 0.0;

        // 사용자 총점
        double userTotalScore = userAverageScore * userRatingCount;

        // 통합 평균
        double combinedAverage = (tmdbTotalScore + userTotalScore) / totalVotes;

        return Math.round(combinedAverage * 100.0) / 100.0;
    }

    // 🆕 드라마 통합 평점 계산 메서드
    public Double calculateDramaCombinedRating(Long dramaId) {
        Drama drama = dramaRepository.findById(dramaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        if (drama.getVoteAverage() == null || drama.getVoteCount() == null) {
            return 0.0;
        }

        // TMDB 총점
        double tmdbTotalScore = drama.getVoteAverage() / 2 * drama.getVoteCount();

        // 🔥 Repository 쿼리로 사용자 평점 계산 (컬렉션 참조 X)
        Double userAverageScore = ratingRepository.findAverageScoreByDramaId(dramaId).orElse(0.0);
        Long userRatingCount = ratingRepository.countByDramaId(dramaId);

        // 전체 투표 수
        int totalVotes = drama.getVoteCount() + userRatingCount.intValue();

        if (totalVotes == 0) return 0.0;

        // 사용자 총점
        double userTotalScore = userAverageScore * userRatingCount;

        // 통합 평균
        double combinedAverage = (tmdbTotalScore + userTotalScore) / totalVotes;

        return Math.round(combinedAverage * 100.0) / 100.0;
    }
}