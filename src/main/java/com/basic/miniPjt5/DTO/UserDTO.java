package com.basic.miniPjt5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.basic.miniPjt5.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse  {
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        public static SimpleResponse  fromEntity(User user) {
            if (user == null) {
                return null;
            }
            return SimpleResponse.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .build();
        }
    }
}
