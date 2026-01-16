package com.kyonggi.diet.auth.withDrawReason.dto;

import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReasonType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithDrawReasonDto {
    private WithDrawReasonType type;
    private String etcReason;
}
