package com.basic.miniPjt5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.basic.miniPjt5.entity.User;


public class UserDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long userId;
        private String name;

        public static SimpleResponse fromEntity(User user) {
            return SimpleResponse.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .build();
        }
    }
}

