package com.basic.miniPjt5.DTO;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class KakaoLoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
    private String name;
    private String email;
}
