package com.kyonggi.diet.review.moderation.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportReasonTypeResponse {
    private String type;
    private String description;
}
