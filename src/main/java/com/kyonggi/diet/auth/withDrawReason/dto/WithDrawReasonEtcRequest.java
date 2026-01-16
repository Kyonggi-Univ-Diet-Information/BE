package com.kyonggi.diet.auth.withDrawReason.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "기타 탈퇴 사유 입력 DTO")
public class WithDrawReasonEtcRequest {
    @Schema(
        description = "기타(ETC) 선택 시 입력하는 탈퇴 사유"
    )
    private String etcReason;
}