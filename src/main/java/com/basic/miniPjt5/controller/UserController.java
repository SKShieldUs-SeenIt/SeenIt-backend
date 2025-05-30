package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<String> profile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String userId = (String) authentication.getPrincipal();
        log.info("✅ 인증된 사용자 ID: {}", userId);

        return ResponseEntity.ok("인증된 사용자 ID: " + userId);
    }

    @PutMapping("/mypage")
    public ResponseEntity<String> updateUserInfo(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String kakaoId = (String) authentication.getPrincipal();
        userService.updateUserInfo(kakaoId, request);

        return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
    }
}
