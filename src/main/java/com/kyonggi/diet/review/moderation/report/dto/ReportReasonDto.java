package com.kyonggi.diet.review.moderation.report.dto;

import com.kyonggi.diet.review.moderation.report.ReportReasonType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportReasonDto {
    private ReportReasonType type;
    private String etcReason;
}
