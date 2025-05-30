package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;

public interface KakaoAuthService {
    KakaoLoginResponse login(String code);
}