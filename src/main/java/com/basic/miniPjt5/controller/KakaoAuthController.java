package com.basic.miniPjt5.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basic.miniPjt5.auth.service.impl.KakaoAuthServiceImpl;  // 네 서비스 위치에 따라 경로 맞춰서
import com.basic.miniPjt5.auth.dto.KakaoLoginResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthServiceImpl kakaoAuthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            log.info("Received code: {}", code);
            KakaoLoginResponse response = kakaoAuthService.login(code);
            log.info("Login success, userId: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
