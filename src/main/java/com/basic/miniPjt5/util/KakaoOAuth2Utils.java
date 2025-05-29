package com.basic.miniPjt5.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoOAuth2Utils {

    public static MultiValueMap<String, String> getKakaoTokenRequest(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", "http://localhost:8080/api/auth/kakao/callback");
        body.add("code", code);
        return body;
    }

    public static MultiValueMap<String, String> getKakaoUserInfoRequest(String accessToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }
}
