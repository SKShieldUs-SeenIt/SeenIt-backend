package com.basic.miniPjt5.auth.service;

import com.basic.miniPjt5.auth.dto.KakaoLoginResponse;

public interface KakaoAuthService {
    KakaoLoginResponse login(String code);
}
