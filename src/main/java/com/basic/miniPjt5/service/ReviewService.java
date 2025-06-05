package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.ReviewDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.Review;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.exception.BusinessException;
import com.basic.miniPjt5.exception.ErrorCode;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.repository.ReviewRepository;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    // 리뷰 생성
    @Transactional
    public ReviewDTO.Response createReview(Long userId, ReviewDTO.CreateRequest requestDto) {
        // 입력 검증
        validateCreateRequest(requestDto);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 중복 리뷰 확인
        checkDuplicateReview(userId, requestDto);

        Review review;

        // 영화 또는 드라마 설정
        if (requestDto.getMovieId() != null) {
            Movie movie = movieRepository.findById(requestDto.getMovieId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

            review = Review.createMovieReview(
                    user,
                    movie,
                    requestDto.getContent(),
                    requestDto.getIsSpoiler()
            );
        } else {
            Drama drama = dramaRepository.findById(requestDto.getDramaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DRAMA_NOT_FOUND));

            review = Review.createDramaReview(
                    user,
                    drama,
                    requestDto.getContent(),
                    requestDto.getIsSpoiler()
            );
        }

        Review savedReview = reviewRepository.save(review);
        return convertToResponseDto(savedReview);
    }

    // 리뷰 수정
    @Transactional
    public ReviewDTO.Response updateReview(Long userId, Long reviewId, ReviewDTO.UpdateRequest requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자 확인
        if (!review.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        // 리뷰 수정
        review.setContent(requestDto.getContent());
        review.setIsSpoiler(requestDto.getIsSpoiler());

        return convertToResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 작성자 확인
        if (!review.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        reviewRepository.delete(review);
    }

    // 리뷰 조회
    public ReviewDTO.Response getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        return convertToResponseDto(review);
    }

    // 영화별 리뷰 목록 조회
    public Page<ReviewDTO.ListResponse> getMovieReviews(Long movieId, Pageable pageable) {
        // 영화 존재 확인
        if (!movieRepository.existsById(movieId)) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }

        Page<Review> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId, pageable);
        return reviews.map(this::convertToListResponseDto);
    }

    // 드라마별 리뷰 목록 조회
    public Page<ReviewDTO.ListResponse> getDramaReviews(Long dramaId, Pageable pageable) {
        // 드라마 존재 확인
        if (!dramaRepository.existsById(dramaId)) {
            throw new BusinessException(ErrorCode.DRAMA_NOT_FOUND);
        }

        Page<Review> reviews = reviewRepository.findByDramaIdOrderByCreatedAtDesc(dramaId, pageable);
        return reviews.map(this::convertToListResponseDto);
    }

    // 사용자별 리뷰 목록 조회
    public Page<ReviewDTO.ListResponse> getUserReviews(Long userId, Pageable pageable) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return reviews.map(this::convertToListResponseDto);
    }

    // 최신 리뷰 목록 조회
    public Page<ReviewDTO.ListResponse> getLatestReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findLatestReviews(pageable);
        return reviews.map(this::convertToListResponseDto);
    }

    // 키워드 검색
    public Page<ReviewDTO.SearchResponse> searchReviews(String keyword, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByKeyword(keyword, pageable);
        return reviews.map(review -> convertToSearchResponseDto(review, keyword));
    }

    // 좋아요 토글 (추가 기능)
    @Transactional
    public ReviewDTO.Response toggleLike(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 실제로는 Like 엔티티를 통해 중복 체크해야 하지만, 여기서는 단순히 증가
        review.increaseLikes();

        return convertToResponseDto(review);
    }

    // 요청 검증
    private void validateCreateRequest(ReviewDTO.CreateRequest requestDto) {
        if ((requestDto.getMovieId() == null && requestDto.getDramaId() == null) ||
                (requestDto.getMovieId() != null && requestDto.getDramaId() != null)) {
            throw new BusinessException(ErrorCode.CONTENT_TYPE_INVALID);
        }
    }

    // 중복 리뷰 확인
    private void checkDuplicateReview(Long userId, ReviewDTO.CreateRequest requestDto) {
        if (requestDto.getMovieId() != null) {
            if (reviewRepository.findByUserIdAndMovieId(userId, requestDto.getMovieId()).isPresent()) {
                throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
            }
        } else {
            if (reviewRepository.findByUserIdAndDramaId(userId, requestDto.getDramaId()).isPresent()) {
                throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
            }
        }
    }

    // DTO 변환
    private ReviewDTO.Response convertToResponseDto(Review review) {
        ReviewDTO.Response dto = new ReviewDTO.Response();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setUsername(review.getUser().getName());
        dto.setUserId(review.getUser().getUserId());
        dto.setLikesCount(review.getLikesCount());
        dto.setIsSpoiler(review.getIsSpoiler());

        if (review.getMovie() != null) {
            dto.setMovieId(review.getMovie().getId());
            dto.setMovieTitle(review.getMovie().getTitle());
            dto.setMoviePosterPath(review.getMovie().getPosterPath());
            dto.setContentType("MOVIE");
        }

        if (review.getDrama() != null) {
            dto.setDramaId(review.getDrama().getId());
            dto.setDramaTitle(review.getDrama().getTitle());
            dto.setDramaPosterPath(review.getDrama().getPosterPath());
            dto.setContentType("DRAMA");
        }

        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setUpdatedAt(review.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return dto;
    }

    private ReviewDTO.ListResponse convertToListResponseDto(Review review) {
        ReviewDTO.ListResponse dto = new ReviewDTO.ListResponse();
        dto.setId(review.getId());
        dto.setContent(review.getContent().length() > 100 ?
                review.getContent().substring(0, 100) + "..." :
                review.getContent());
        dto.setUsername(review.getUser().getName());
        dto.setUserId(review.getUser().getUserId());
        dto.setLikesCount(review.getLikesCount());
        dto.setIsSpoiler(review.getIsSpoiler());
        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        if (review.getMovie() != null) {
            dto.setContentType("MOVIE");
            dto.setContentTitle(review.getMovie().getTitle());
        } else {
            dto.setContentType("DRAMA");
            dto.setContentTitle(review.getDrama().getTitle());
        }

        return dto;
    }

    private ReviewDTO.SearchResponse convertToSearchResponseDto(Review review, String keyword) {
        ReviewDTO.SearchResponse dto = new ReviewDTO.SearchResponse();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setUsername(review.getUser().getName());
        dto.setLikesCount(review.getLikesCount());
        dto.setIsSpoiler(review.getIsSpoiler());
        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // 검색어 하이라이트 처리 (간단한 예시)
        dto.setHighlightedContent(highlightKeyword(review.getContent(), keyword));

        if (review.getMovie() != null) {
            dto.setContentType("MOVIE");
            dto.setContentTitle(review.getMovie().getTitle());
            dto.setPosterPath(review.getMovie().getPosterPath());
        } else {
            dto.setContentType("DRAMA");
            dto.setContentTitle(review.getDrama().getTitle());
            dto.setPosterPath(review.getDrama().getPosterPath());
        }

        return dto;
    }

    // 검색어 하이라이트 처리 (간단한 예시)
    private String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null) return text;
        return text.replaceAll("(?i)" + keyword, "<mark>$0</mark>");
    }
}