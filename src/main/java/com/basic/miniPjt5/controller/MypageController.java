package com.basic.miniPjt5.controller;

import com.basic.miniPjt5.dto.UserUpdateRequest;
import com.basic.miniPjt5.entity.User;
import com.basic.miniPjt5.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
public class MypageController {

    private final UserService userService;

    public MypageController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {

        User updatedUser = userService.updateUser(userId, request.getName(), request.getPreferredGenres());
        return ResponseEntity.ok(updatedUser);
    }
}
