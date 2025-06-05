package com.basic.miniPjt5.service;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.entity.UserWatched;
import com.basic.miniPjt5.repository.UserWatchedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserWatchedService {

    private final UserWatchedRepository userWatchedRepository;
    private final UserService userService;

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

    // 간단한 통계 DTO
    public record WatchedStats(long totalMovies, long totalDramas, long totalContents) {}
}