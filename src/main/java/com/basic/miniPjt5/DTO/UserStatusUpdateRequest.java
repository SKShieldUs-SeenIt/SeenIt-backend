package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 📥 관리자 사용자 상태 변경 요청 DTO
@Getter
@NoArgsConstructor
@Schema(description = "관리자용 사용자 상태 변경 요청 DTO")
public class UserStatusUpdateRequest {

    @NotNull(message = "상태 값은 필수입니다")
    @Schema(
            description = "변경할 사용자 상태",
            example = "SUSPENDED",
            allowableValues = {"ACTIVE", "SUSPENDED", "WITHDRAWN", "DELETED"}
    )
    private UserStatus status;
}
