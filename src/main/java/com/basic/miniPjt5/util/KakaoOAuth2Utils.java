package com.basic.miniPjt5.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Utils {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 🔐 인가 코드로 카카오 액세스 토큰 요청 파라미터 생성
     */
    public MultiValueMap<String, String> getKakaoTokenRequest(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("인가 코드(code)가 비어 있습니다.");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        return body;
    }

    /**
     * 🧾 액세스 토큰을 이용해 사용자 정보 요청 헤더 생성
     */
    public MultiValueMap<String, String> getKakaoUserInfoRequest(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("액세스 토큰(accessToken)이 비어 있습니다.");
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }
}
