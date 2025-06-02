package com.basic.miniPjt5.security;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 로그인을 위한 CustomUserDetailsService
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        log.debug("Loading user by kakaoId: {}", kakaoId);
        
        User user = userRepository.findByKakaoIdAndStatus(kakaoId, UserStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("User not found with kakaoId: {}", kakaoId);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + kakaoId);
                });
        
        log.debug("Successfully loaded user: {} ({})", user.getName(), user.getKakaoId());
        return new CustomUserDetails(user);
    }

    /**
     * 사용자 ID로 UserDetails 조회
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Loading user by userId: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with userId: {}", userId);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                });
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("User is not active: {} (status: {})", userId, user.getStatus());
            throw new UsernameNotFoundException("비활성화된 사용자입니다: " + userId);
        }
        
        log.debug("Successfully loaded user: {} ({})", user.getName(), user.getUserId());
        return new CustomUserDetails(user);
    }
}