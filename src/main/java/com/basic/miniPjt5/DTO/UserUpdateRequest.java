package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

//📥 사용자 정보 수정 요청 DTO
@Getter
@Setter
@Schema(description = "사용자 정보 수정 요청 DTO")
public class UserUpdateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Schema(description = "변경할 사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "선호 장르 리스트", example = "[\"드라마\", \"코미디\", \"스릴러\"]")
    private List<String> preferredGenres;
}
