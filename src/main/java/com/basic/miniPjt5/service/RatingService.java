package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.RatingDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.Rating;
import com.basic.miniPjt5.entity.User;
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

        ratingRepository.delete(rating);
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

        Double averageScore = ratingRepository.findAverageScoreByMovieId(movieId).orElse(0.0);
        Long ratingCount = ratingRepository.countByMovieId(movieId);

        return RatingDTO.AverageResponse.builder()
                .contentId(movieId)
                .contentType("Movie")
                .contentTitle(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .averageScore(roundToTwoDecimals(averageScore))
                .ratingCount(ratingCount)
                .tmdbRating(movie.getVoteAverage())
                .build();

    }

    // 드라마 평균 별점 조회
    public RatingDTO.AverageResponse getDramaAverageRating(Long dramaId) {
        Drama drama = dramaRepository.findById(dramaId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

        Double averageScore = ratingRepository.findAverageScoreByDramaId(dramaId).orElse(0.0);
        Long ratingCount = ratingRepository.countByDramaId(dramaId);

        return RatingDTO.AverageResponse.builder()
                .contentId(dramaId)
                .contentType("DRAMA")
                .contentTitle(drama.getTitle())
                .posterPath(drama.getPosterPath())
                .averageScore(roundToTwoDecimals(averageScore))
                .ratingCount(ratingCount)
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
    public Map<Integer, Long> getScoreDistribution(Long movieId, Long dramaId) {
        Object[][] distribution = null;

        if (movieId != null) {
            // 영화 존재 확인
            if (!movieRepository.existsById(movieId)) {
                throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
            }
            distribution = ratingRepository.findScoreDistributionByMovieId(movieId);
        } else if (dramaId != null) {
            // 드라마 존재 확인
            if (!dramaRepository.existsById(dramaId)) {
                throw new BusinessException(ErrorCode.DRAMA_NOT_FOUND);
            }
            distribution = ratingRepository.findScoreDistributionByDramaId(dramaId);
        }

        Map<Integer, Long> result = new HashMap<>();

        // 1~10점 초기화
        for (int i = 1; i <= 10; i++) {
            result.put(i, 0L);
        }

        // 실제 데이터 입력
        if (distribution != null) {
            for (Object[] row : distribution) {
                Integer score = (Integer) row[0];
                Long count = ((Number) row[1]).longValue();
                result.put(score, count);
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
            dto.setMovieTitle(rating.getMovie().getTitle());
        }

        if (rating.getDrama() != null) {
            dto.setDramaId(rating.getDrama().getId());
            dto.setDramaTitle(rating.getDrama().getTitle());
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
}