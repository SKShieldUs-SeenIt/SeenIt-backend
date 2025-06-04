package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//📦 사용자 상세 응답 DTO - 마이페이지(profile), 사용자 정보 수정 응답 등에 사용
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {

    /*@Schema(description = "사용자 ID", example = "1")
    private Long userId;*/

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "선호 장르", example = "드라마,스릴러")
    private String preferredGenres;

    /*@Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;*/

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                //.userId(user.getUserId())
                .name(user.getName())
                .preferredGenres(user.getPreferredGenres())
                //.email(user.getEmail())
                //.profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
