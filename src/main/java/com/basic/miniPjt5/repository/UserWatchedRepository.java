package com.basic.miniPjt5.repository;

import com.basic.miniPjt5.entity.UserWatched;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchedRepository extends JpaRepository<UserWatched, Long> {

    // 특정 콘텐츠 시청 여부 확인
    boolean existsByUserIdAndContentTypeAndContentId(
            Long userId, UserWatched.ContentType contentType, Long contentId);

    // 특정 콘텐츠 시청 기록 조회
    Optional<UserWatched> findByUserIdAndContentTypeAndContentId(
            Long userId, UserWatched.ContentType contentType, Long contentId);

    // 사용자가 시청한 특정 타입 콘텐츠 목록 (최근 순)
    Page<UserWatched> findByUserIdAndContentTypeOrderByWatchedAtDesc(
            Long userId, UserWatched.ContentType contentType, Pageable pageable);

    // 사용자가 시청한 모든 콘텐츠 목록 (최근 순)
    Page<UserWatched> findByUserIdOrderByWatchedAtDesc(Long userId, Pageable pageable);

    // 사용자가 시청한 특정 타입 콘텐츠 ID 목록
    @Query("SELECT uw.contentId FROM UserWatched uw WHERE uw.userId = :userId AND uw.contentType = :contentType")
    List<Long> findContentIdsByUserIdAndContentType(
            @Param("userId") Long userId, 
            @Param("contentType") UserWatched.ContentType contentType);

    // 시청 기록 삭제
    void deleteByUserIdAndContentTypeAndContentId(
            Long userId, UserWatched.ContentType contentType, Long contentId);

    // 사용자별 시청 통계
    long countByUserIdAndContentType(Long userId, UserWatched.ContentType contentType);
    
    long countByUserId(Long userId);
}