package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.UserAdminResponse;
import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                    // **필요한 필드만 업데이트**
                    existingUser.setName(kakaoUser.getName());
                    existingUser.setEmail(kakaoUser.getEmail());
                    existingUser.setProfileImageUrl(kakaoUser.getProfileImageUrl());
                    existingUser.setStatus(UserStatus.ACTIVE);
                    return existingUser; // JPA Dirty Checking
                })
                .orElseGet(() -> userRepository.save(kakaoUser));
    }

    /**
     * ✅ 사용자 정보 수정 - Kakao ID 기준 (UserController에서 사용)
     */
    @Transactional
    public void updateUserInfo(String kakaoId, UserUpdateRequest request) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(request.getName());
        user.setPreferredGenres(request.getPreferredGenres());
    }

    /**
     * ❌ 사용자 탈퇴 (소프트 삭제)
     */
    @Transactional
    public void deactivateUser(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setStatus(UserStatus.DELETED);
    }

    //서비스 메서드
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserAdminResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getStatus()
                ))
                .toList();
    }

    // 관리자용 상태 변경 메서드
    @Transactional
    public User changeUserStatus(Long userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setStatus(newStatus);
        return user;
    }



}
