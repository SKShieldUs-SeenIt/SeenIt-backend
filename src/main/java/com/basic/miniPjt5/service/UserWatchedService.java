package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.UserWatchedDTO;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.entity.UserWatched;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import com.basic.miniPjt5.repository.UserWatchedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserWatchedService {

    private final UserWatchedRepository userWatchedRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;

    /**
     * 콘텐츠를 시청했다고 표시
     */
    @Transactional
    public void markAsWatched(String kakaoId, UserWatched.ContentType contentType, Long contentId) {
        User user = userService.findByKakaoId(kakaoId);

        // 이미 시청 기록이 있는지 확인
        if (userWatchedRepository.existsByUserIdAndContentTypeAndContentId(
                user.getUserId(), contentType, contentId)) {
            log.info("이미 시청 기록이 존재합니다 - User: {}, ContentType: {}, ContentId: {}",
                    kakaoId, contentType, contentId);
            return; // 이미 있으면 그냥 리턴
        }

        UserWatched userWatched = UserWatched.create(user.getUserId(), contentType, contentId);
        userWatchedRepository.save(userWatched);

        log.info("✅ 시청 완료 표시 - User: {}, ContentType: {}, ContentId: {}",
                kakaoId, contentType, contentId);
    }

    /**
     * 시청 여부 확인
     */
    public boolean isWatched(String kakaoId, UserWatched.ContentType contentType, Long contentId) {
        User user = userService.findByKakaoId(kakaoId);
        return userWatchedRepository.existsByUserIdAndContentTypeAndContentId(
                user.getUserId(), contentType, contentId);
    }

    /**
     * 시청 기록 삭제 (안 봤다고 표시)
     */
    @Transactional
    public void removeFromWatched(String kakaoId, UserWatched.ContentType contentType, Long contentId) {
        User user = userService.findByKakaoId(kakaoId);
        userWatchedRepository.deleteByUserIdAndContentTypeAndContentId(
                user.getUserId(), contentType, contentId);

        log.info("❌ 시청 기록 삭제 - User: {}, ContentType: {}, ContentId: {}",
                kakaoId, contentType, contentId);
    }

    /**
     * 사용자가 시청한 영화 목록 조회
     */
    public Page<UserWatched> getWatchedMovies(String kakaoId, int page, int size) {
        User user = userService.findByKakaoId(kakaoId);
        Pageable pageable = PageRequest.of(page, size);
        return userWatchedRepository.findByUserIdAndContentTypeOrderByWatchedAtDesc(
                user.getUserId(), UserWatched.ContentType.MOVIE, pageable);
    }

    /**
     * 사용자가 시청한 드라마 목록 조회
     */
    public Page<UserWatched> getWatchedDramas(String kakaoId, int page, int size) {
        User user = userService.findByKakaoId(kakaoId);
        Pageable pageable = PageRequest.of(page, size);
        return userWatchedRepository.findByUserIdAndContentTypeOrderByWatchedAtDesc(
                user.getUserId(), UserWatched.ContentType.DRAMA, pageable);
    }

    /**
     * 사용자가 시청한 모든 콘텐츠 목록 조회
     */
    public Page<UserWatched> getAllWatchedContents(String kakaoId, int page, int size) {
        User user = userService.findByKakaoId(kakaoId);
        Pageable pageable = PageRequest.of(page, size);
        return userWatchedRepository.findByUserIdOrderByWatchedAtDesc(user.getUserId(), pageable);
    }

    /**
     * 사용자가 시청한 영화 ID 목록
     */
    public List<Long> getWatchedMovieIds(String kakaoId) {
        User user = userService.findByKakaoId(kakaoId);
        return userWatchedRepository.findContentIdsByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.MOVIE);
    }

    /**
     * 사용자가 시청한 드라마 ID 목록
     */
    public List<Long> getWatchedDramaIds(String kakaoId) {
        User user = userService.findByKakaoId(kakaoId);
        return userWatchedRepository.findContentIdsByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.DRAMA);
    }

    /**
     * 시청 통계 조회
     */
    public WatchedStats getWatchedStats(String kakaoId) {
        User user = userService.findByKakaoId(kakaoId);

        long totalMovies = userWatchedRepository.countByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.MOVIE);
        long totalDramas = userWatchedRepository.countByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.DRAMA);
        long totalContents = userWatchedRepository.countByUserId(user.getUserId());

        return new WatchedStats(totalMovies, totalDramas, totalContents);
    }

    /**
     * 시청 통계를 DTO로 반환
     */
    public UserWatchedDTO.StatsResponse getWatchedStatsDto(String kakaoId) {
        User user = userService.findByKakaoId(kakaoId);

        long totalMovies = userWatchedRepository.countByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.MOVIE);
        long totalDramas = userWatchedRepository.countByUserIdAndContentType(
                user.getUserId(), UserWatched.ContentType.DRAMA);
        long totalContents = userWatchedRepository.countByUserId(user.getUserId());

        // 최근 시청한 콘텐츠
        String recentlyWatched = userWatchedRepository.findFirstByUserIdOrderByWatchedAtDesc(user.getUserId())
                .map(watched -> getContentTitle(watched))
                .orElse("없음");

        // 이번 달 시청 수
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long thisMonthWatched = userWatchedRepository.countByUserIdAndWatchedAtAfter(user.getUserId(), startOfMonth);

        // 가장 많이 본 장르 (간단하게 구현)
        String favoriteGenre = getFavoriteGenre(user.getUserId());

        // 평균 콘텐츠 평점
        Double averageRating = calculateAverageContentRating(user.getUserId());

        return UserWatchedDTO.StatsResponse.builder()
                .totalMovies(totalMovies)
                .totalDramas(totalDramas)
                .totalContents(totalContents)
                .recentlyWatched(recentlyWatched)
                .thisMonthWatched(thisMonthWatched)
                .favoriteGenre(favoriteGenre)
                .averageContentRating(averageRating)
                .build();
    }

    private String getContentTitle(UserWatched userWatched) {
        switch (userWatched.getContentType()) {
            case MOVIE:
                return movieRepository.findById(userWatched.getContentId())
                        .map(Movie::getTitle)
                        .orElse("알 수 없는 영화");
            case DRAMA:
                return dramaRepository.findById(userWatched.getContentId())
                        .map(Drama::getTitle)
                        .orElse("알 수 없는 드라마");
            default:
                return "알 수 없는 콘텐츠";
        }
    }

    private String getFavoriteGenre(Long userId) {
        // 간단한 구현 - 실제로는 더 복잡한 쿼리 필요
        return "액션"; // 임시값
    }

    private Double calculateAverageContentRating(Long userId) {
        List<UserWatched> watchedList = userWatchedRepository.findByUserIdOrderByWatchedAtDesc(
                userId, Pageable.unpaged()).getContent();

        if (watchedList.isEmpty()) return null;

        double totalRating = 0.0;
        int count = 0;

        for (UserWatched watched : watchedList) {
            Double rating = null;
            if (watched.getContentType() == UserWatched.ContentType.MOVIE) {
                rating = movieRepository.findById(watched.getContentId())
                        .map(Movie::getCombinedRating)
                        .orElse(null);
            } else if (watched.getContentType() == UserWatched.ContentType.DRAMA) {
                rating = dramaRepository.findById(watched.getContentId())
                        .map(Drama::getCombinedRating)
                        .orElse(null);
            }

            if (rating != null) {
                totalRating += rating;
                count++;
            }
        }

        return count > 0 ? Math.round((totalRating / count) * 100.0) / 100.0 : null;
    }

    // 기존 record도 유지 (하위 호환성)
    public record WatchedStats(long totalMovies, long totalDramas, long totalContents) {}
}