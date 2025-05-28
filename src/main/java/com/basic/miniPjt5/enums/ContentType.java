package com.basic.miniPjt5.enums;

public enum ContentType {
    MOVIE("영화", "영화 콘텐츠"),
    DRAMA("드라마", "드라마 콘텐츠");

    private final String displayName;
    private final String description;

    ContentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}