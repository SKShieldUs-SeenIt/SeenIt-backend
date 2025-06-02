package com.basic.miniPjt5.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponse {
    private String accessToken;
    private boolean isNewUser;
}
