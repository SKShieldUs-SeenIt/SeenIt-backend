package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 📦 사용자 상세 응답 DTO
 * - 마이페이지(profile), 사용자 정보 수정 응답 등에 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long userId;
    private String name;
    private String preferredGenres;
    private String email;
    private String profileImageUrl;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .preferredGenres(user.getPreferredGenres())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}

