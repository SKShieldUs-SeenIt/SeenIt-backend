package com.basic.miniPjt5.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT 인증과 관련된 헬퍼 메서드들을 제공하는 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationHelper {

    /**
     * 현재 인증된 사용자의 ID를 반환
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) principal).getUserId();
            log.debug("Current user ID: {}", userId);
            return userId;
        }
        
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new IllegalStateException("익명 사용자입니다. 로그인이 필요합니다.");
        }
        
        log.error("Unexpected principal type: {}", principal.getClass());
        throw new IllegalStateException("인증 정보를 확인할 수 없습니다.");
    }

    /**
     * 현재 인증된 사용자의 CustomUserDetails를 반환
     */
    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        
        throw new IllegalStateException("CustomUserDetails를 찾을 수 없습니다.");
    }

    /**
     * 현재 인증된 사용자의 카카오 ID를 반환
     */
    public String getCurrentKakaoId() {
        return getCurrentUserDetails().getKakaoId();
    }

    /**
     * 현재 인증된 사용자의 이름을 반환
     */
    public String getCurrentUserName() {
        return getCurrentUserDetails().getName();
    }

    /**
     * UserDetails에서 사용자 ID를 안전하게 추출
     */
    public Long extractUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        
        log.error("Cannot extract user ID from UserDetails: {}", userDetails.getClass());
        throw new IllegalArgumentException("올바르지 않은 UserDetails 타입입니다.");
    }

    /**
     * 특정 사용자가 현재 인증된 사용자와 동일한지 확인
     */
    public boolean isCurrentUser(Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            return currentUserId.equals(userId);
        } catch (Exception e) {
            log.debug("Failed to get current user ID: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 현재 사용자가 인증되어 있는지 확인
     */
    public boolean isAuthenticated() {
        try {
            getCurrentUserId();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}