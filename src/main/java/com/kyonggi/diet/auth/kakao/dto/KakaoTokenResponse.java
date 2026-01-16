package com.kyonggi.diet.auth.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoTokenResponse {
    private String accessToken;
    private String refreshToken;
}
