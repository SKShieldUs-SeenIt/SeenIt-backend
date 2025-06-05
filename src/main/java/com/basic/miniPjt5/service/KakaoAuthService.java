package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.DTO.KakaoUserInfo;

public interface KakaoAuthService {
    KakaoLoginResponse login(String code);
    String getAccessToken(String code);   // Get the access token
    KakaoUserInfo getUserInfo(String accessToken);  // 반환 타입을 KakaoUserInfo로 수정
    String createJwtToken(String userInfo);  // Create JWT token
}
