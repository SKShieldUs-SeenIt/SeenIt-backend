package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.KakaoLoginResponse;
import com.basic.miniPjt5.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /*@PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        log.info("✅ 카카오 로그인 요청 수신 - code: {}", code);

        if (code == null || code.isEmpty()) {
            log.warn("⚠️ 인가 코드가 비어있거나 전달되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("인가 코드(code)가 없습니다.");
        }

        try {
            KakaoLoginResponse response = kakaoAuthService.login(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 로그인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }*/

    @GetMapping("/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) {
        log.info("✅ 카카오 인가 코드 수신 - code: {}", code);

        try {
            // 🔄 인가 코드로 access token 받고, 사용자 인증 처리 (JWT 생성까지)
            KakaoLoginResponse loginResponse = kakaoAuthService.login(code);
            String jwtToken = loginResponse.getAccessToken();

            // ✅ 프론트로 redirect (JWT 전달)
            String redirectUrl = "http://localhost:5173/kakao/complete?token=" + jwtToken;

            response.sendRedirect(redirectUrl); // 302 Redirect 자동 처리됨

        } catch (Exception e) {
            log.error("❌ 로그인 처리 중 오류 발생", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 실패");
            } catch (IOException ioException) {
                log.error("❌ 응답 실패", ioException);
            }
        }
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
