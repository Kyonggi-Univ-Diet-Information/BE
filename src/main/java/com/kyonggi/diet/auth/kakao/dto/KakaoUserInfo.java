package com.kyonggi.diet.auth.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfo {
    private String email;
    private String nickname;
}
