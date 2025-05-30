package com.basic.miniPjt5.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    private String name;

    private String preferredGenres;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredGenres() {
        return preferredGenres;
    }

    public void setPreferredGenres(String preferredGenres) {
        this.preferredGenres = preferredGenres;
    }
}
