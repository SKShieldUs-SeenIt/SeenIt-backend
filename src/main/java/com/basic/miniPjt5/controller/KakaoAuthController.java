package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 프론트에서 전달된 인가 코드를 통해 로그인 처리
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        try {
            log.info("✅ 인가 코드 수신: {}", code);

            KakaoLoginResponse response = kakaoAuthService.login(code);

            log.info("✅ 로그인 성공 - 유저 ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 로그인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 서버 테스트용 API
     */
    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
