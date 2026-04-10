package com.kyonggi.diet.member.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckResponse {
    private boolean available;
    private String message;
}
