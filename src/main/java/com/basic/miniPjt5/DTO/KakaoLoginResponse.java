package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

// 📦 카카오 로그인 응답 DTO
@Getter
@Builder
@Schema(description = "카카오 로그인 응답 DTO")
public class KakaoLoginResponse {

    @Schema(description = "발급된 JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsIn...")
    private String accessToken;

    @Schema(description = "신규 회원 여부", example = "true")
    private boolean isNewUser;
}
