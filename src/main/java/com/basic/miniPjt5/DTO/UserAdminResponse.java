package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAdminResponse {
    private Long userId;
    private String name;
    private UserStatus status;
}
