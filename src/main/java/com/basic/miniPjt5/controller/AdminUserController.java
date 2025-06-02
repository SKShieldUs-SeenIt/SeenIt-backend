package com.basic.miniPjt5.controller;

import java.util.List;
import com.basic.miniPjt5.DTO.UserAdminResponse;
import com.basic.miniPjt5.DTO.UserStatusUpdateRequest;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    // 사용자 목록 조회 API 추가
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserAdminResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 관리자 사용자 상태 변경
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        try {
            User updatedUser = userService.changeUserStatus(userId, request.getStatus());
            return ResponseEntity.ok("✅ 사용자 상태가 " + updatedUser.getStatus() + "로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
