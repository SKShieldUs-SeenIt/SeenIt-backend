package com.basic.miniPjt5.enums;

public enum UserStatus {
    ACTIVE("활성", "정상적으로 서비스 이용 가능"),
    SUSPENDED("정지", "관리자에 의해 이용이 정지된 상태"),
    WITHDRAWN("탈퇴", "사용자가 탈퇴한 상태"),     // ✅ 소프트 삭제 (상태만 탈퇴 상태)
    DELETED("삭제", "관리자에 의해 삭제된 상태"); // 🔥 하드 삭제 (DB 유저 정보 제거)

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}