package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.DTO.UserResponse;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 🔐 인증된 사용자 ID 추출 (중복 제거용)
    private String extractKakaoIdOrThrow(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }
        return (String) authentication.getPrincipal();
    }

    // ✅ GET: 현재 로그인한 유저 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String kakaoId = extractKakaoIdOrThrow(authentication);
        log.info("✅ 사용자 정보 조회 요청 - kakaoId: {}", kakaoId);

        User user = userService.findByKakaoId(kakaoId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // ✅ PUT: 유저 정보 수정
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        String kakaoId = extractKakaoIdOrThrow(authentication);
        log.info("🔄 사용자 정보 수정 요청 - kakaoId: {}", kakaoId);

        User updatedUser = userService.updateUserInfo(kakaoId, request);
        if (updatedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    // ✅ DELETE: 사용자 탈퇴 (Soft Delete 방식)
    @DeleteMapping("/me")
    public ResponseEntity<String> withdrawUser(Authentication authentication) {
        String kakaoId = extractKakaoIdOrThrow(authentication);
        log.info("❌ 사용자 탈퇴 요청 - kakaoId: {}", kakaoId);

        userService.deactivateUser(kakaoId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
