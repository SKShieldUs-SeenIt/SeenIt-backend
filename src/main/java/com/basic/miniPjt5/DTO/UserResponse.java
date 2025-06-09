package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//📦 사용자 상세 응답 DTO - 마이페이지(profile), 사용자 정보 수정 응답 등에 사용
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "선호 장르 목록", example = "[\"드라마\", \"스릴러\"]")
    private List<String> preferredGenres;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private UserStatus status;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .preferredGenres(user.getPreferredGenres())
                .profileImageUrl(user.getProfileImageUrl())
                .status(user.getStatus())
                .build();
    }
}
