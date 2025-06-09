package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.DTO.UserAdminResponse;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.exception.UserSuspendedException;
import com.basic.miniPjt5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";

    @Transactional(readOnly = true)
    public User findByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    public boolean existsByKakaoId(String kakaoId) {
        return userRepository.existsByKakaoId(kakaoId);
    }

    // 🔄 카카오 사용자 저장 또는 업데이트
    @Transactional
    public User saveOrUpdate(User kakaoUser) {
        return userRepository.findByKakaoId(kakaoUser.getKakaoId())
                .map(existingUser -> {
                    existingUser.setName(kakaoUser.getName());
                    existingUser.setProfileImageUrl(kakaoUser.getProfileImageUrl());
                    existingUser.setStatus(UserStatus.ACTIVE);
                    return existingUser;
                })
                .orElseGet(() -> userRepository.save(kakaoUser));
    }

    // ✅ 사용자 정보 수정 - Kakao ID 기준 (UserController에서 사용)
    @Transactional
    public User updateUserInfo(String kakaoId, UserUpdateRequest request) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        user.setName(request.getName());
        user.setPreferredGenres(request.getPreferredGenres());
        return user;
    }

    // ❌ 사용자 탈퇴 (소프트 삭제)
    @Transactional
    public void deactivateUser(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        user.setStatus(UserStatus.DELETED);
    }

    // 🛠️ 관리자용: 전체 사용자 목록 조회
    @Transactional(readOnly = true)
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserAdminResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getStatus()
                ))
                .toList();
    }

    // 🛠️ 관리자용: 사용자 상태 변경
    @Transactional
    public User changeUserStatus(Long userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (newStatus == UserStatus.DELETED) {
            userRepository.delete(user); // ✅ 하드 삭제
            return null; // 컨트롤러에서 처리
        }

        // ✅ 소프트 삭제 및 기타 상태 변경
        user.changeStatus(newStatus, "관리자 변경");
        return user;
    }

    // 🚫 정지된 유저인지 검사하는 유틸 함수
    public void validateActiveUser(User user) {
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UserSuspendedException();
        }
    }
}
