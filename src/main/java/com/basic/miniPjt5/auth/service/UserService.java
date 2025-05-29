package com.basic.miniPjt5.auth.service;

import com.basic.miniPjt5.domain.User;
import com.basic.miniPjt5.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdate(User kakaoUser) {
        return userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
                    User updatedUser = User.builder()
                            .id(existingUser.getId()) // 기존 ID 유지
                            .kakaoId(kakaoUser.getKakaoId())
                            .name(kakaoUser.getName())
                            .email(kakaoUser.getEmail())
                            .profileImageUrl(kakaoUser.getProfileImageUrl())
                            .status(User.UserStatus.ACTIVE)
                            .build();
                    return userRepository.save(updatedUser);
                })
                .orElseGet(() -> userRepository.save(kakaoUser)); // 신규 사용자 저장
    }
}
