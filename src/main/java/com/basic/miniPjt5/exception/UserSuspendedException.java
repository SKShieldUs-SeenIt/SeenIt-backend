package com.basic.miniPjt5.exception;

public class UserSuspendedException extends BusinessException {
    public UserSuspendedException() {
        super(ErrorCode.USER_SUSPENDED);
    }
}
