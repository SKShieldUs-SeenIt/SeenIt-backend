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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    //GET 현재 로그인한 유저 정보 조회 (/users/me 역할)
    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String kakaoId = (String) authentication.getPrincipal();
        log.info("✅ 인증된 사용자 ID: {}", kakaoId);

        User user = userService.findByKakaoId(kakaoId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        UserResponse response = new UserResponse(user.getId(), user.getName(), user.getPreferredGenres());
        return ResponseEntity.ok(response);
    }

    //PUT 유저 정보 수정
    @PutMapping("/mypage")
    public ResponseEntity<?> updateUserInfo(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String kakaoId = (String) authentication.getPrincipal();
        log.info("🔄 사용자 정보 수정 요청, 사용자ID: {}", kakaoId);

        User updatedUser = userService.updateUserInfo(kakaoId, request);
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getPreferredGenres());
        return ResponseEntity.ok(response);
    }

    //사용자 탈퇴 (Soft Delete 방식)
    @DeleteMapping("/me")
    public ResponseEntity<?> withdrawUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String kakaoId = (String) authentication.getPrincipal();
        log.info("❌ 사용자 탈퇴 요청, 사용자ID: {}", kakaoId);

        userService.deactivateUser(kakaoId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
