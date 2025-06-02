package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserStatusUpdateRequest {
    private UserStatus status;
}
