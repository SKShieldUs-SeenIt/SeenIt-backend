package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 🔄 카카오 사용자 저장 또는 업데이트
     */
    @Transactional
    public User saveOrUpdate(User kakaoUser) {
        return userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
                    User updatedUser = User.builder()
                            .userId(existingUser.getUserId()) // 기존 ID 유지
                            .kakaoId(kakaoUser.getKakaoId())
                            .name(kakaoUser.getName())
                            .email(kakaoUser.getEmail())
                            .profileImageUrl(kakaoUser.getProfileImageUrl())
                            .status(UserStatus.ACTIVE)
                            .preferredGenres(existingUser.getPreferredGenres())
                            .joinDate(existingUser.getJoinDate())
                            .build();
                    return userRepository.save(updatedUser);
                })
                .orElseGet(() -> userRepository.save(kakaoUser)); // 신규 사용자 저장
    }

    /**
     * ✏️ 사용자 정보 수정 (kakaoId 기준)
     */
    @Transactional
    public void updateUserInfo(String kakaoId, UserUpdateRequest request) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(request.getName());
        user.setPreferredGenres(request.getPreferredGenres());
    }

    /**
     * ✏️ 사용자 정보 수정 (userId 기준 - 추가적 사용 대비)
     */
    @Transactional
    public User updateUser(Long userId, String name, String preferredGenres) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateProfile(name, user.getProfileImageUrl(), preferredGenres); // 커스텀 메서드일 경우 엔티티에 정의돼 있어야 함
        return user;
    }
}
