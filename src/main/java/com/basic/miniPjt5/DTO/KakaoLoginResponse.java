package com.basic.miniPjt5.DTO;

import lombok.Getter;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
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


