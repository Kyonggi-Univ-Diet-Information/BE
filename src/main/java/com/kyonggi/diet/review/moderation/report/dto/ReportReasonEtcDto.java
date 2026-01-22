package com.kyonggi.diet.review.moderation.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportReasonEtcDto {
    @Schema(
            description = "기타(ETC) 선택 시 입력하는 탈퇴 사유"
    )
    private String etcReason;
}