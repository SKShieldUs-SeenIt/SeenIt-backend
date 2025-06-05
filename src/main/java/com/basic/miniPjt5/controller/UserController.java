package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.DTO.UserUpdateRequest;
import com.basic.miniPjt5.DTO.UserResponse;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.security.UserPrincipal; // ✅ 추가
import com.basic.miniPjt5.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "마이페이지", description = "사용자 개인의 정보 조회, 변경, 탈퇴 요청 API")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // ✅ GET: 현재 로그인한 사용자 정보 조회
    @Operation(summary = "현재 로그인한 사용자 정보 조회", description = "JWT 토큰을 기반으로 현재 로그인한 사용자의 정보를 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        String kakaoId = userPrincipal.getKakaoId();
        log.info("✅ 사용자 정보 조회 요청 - kakaoId: {}", kakaoId);

        User user = userService.findByKakaoId(kakaoId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // ✅ PUT: 사용자 정보 수정
    @Operation(summary = "사용자 정보 수정", description = "요청 본문에 포함된 정보로 현재 로그인한 사용자의 닉네임, 선호 장르 등을 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 본문"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        String kakaoId = userPrincipal.getKakaoId();
        log.info("🔄 사용자 정보 수정 요청 - kakaoId: {}", kakaoId);

        User updatedUser = userService.updateUserInfo(kakaoId, request);
        if (updatedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    // ✅ DELETE: 사용자 탈퇴 요청(Soft Delete 방식)
    @Operation(summary = "회원 탈퇴 요청", description = "현재 로그인한 사용자의 계정을 비활성화(soft delete) 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/me")
    public ResponseEntity<String> withdrawUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        String kakaoId = userPrincipal.getKakaoId();
        log.info("❌ 사용자 탈퇴 요청 - kakaoId: {}", kakaoId);

        userService.deactivateUser(kakaoId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
