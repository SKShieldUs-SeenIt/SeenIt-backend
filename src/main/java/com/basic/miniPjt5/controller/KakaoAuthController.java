package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 프론트에서 전달된 인가 코드를 통해 로그인 처리
     */
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        log.info("✅ 카카오 로그인 요청 수신 - code: {}", code);

        try {
            KakaoLoginResponse response = kakaoAuthService.login(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 로그인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        log.info("카카오 콜백 - code: {}", code);
        // 여기서는 보통 인가 코드를 프론트로 리다이렉트하거나 바로 토큰 교환 처리 가능
        return ResponseEntity.ok("인가 코드 받음: " + code);
    }

    /**
     * 서버 테스트용 API
     */
    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
