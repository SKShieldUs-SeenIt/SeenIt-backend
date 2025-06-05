package com.basic.miniPjt5.controller;

import java.util.List;
import com.basic.miniPjt5.enums.UserStatus;
import com.basic.miniPjt5.DTO.UserAdminResponse;
import com.basic.miniPjt5.DTO.UserStatusUpdateRequest;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "관리자", description = "전체 사용자 조회 및 상태 변경 관련 API")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    // ✅ GET: 전체 사용자 목록 조회
    @Operation(summary = "전체 사용자 목록 조회 (관리자)", description = "관리자가 모든 사용자 정보를 확인할 수 있는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserAdminResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ✅ PUT: 사용자 상태 변경 (예: ACTIVE → SUSPENDED)
    @Operation(summary = "사용자 상태 변경 (관리자)", description = "지정한 사용자 ID의 상태를 변경합니다. 예: ACTIVE, SUSPENDED, WITHDRAWN, DELETED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음")
    })
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        try {
            UserStatus status = request.getStatus();
            User updatedUser = userService.changeUserStatus(userId, status);

            if (status == UserStatus.DELETED) {
                return ResponseEntity.ok("🗑️ 사용자 계정이 완전히 삭제되었습니다.");
            }

            return ResponseEntity.ok("✅ 사용자 상태가 " + status + "로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}