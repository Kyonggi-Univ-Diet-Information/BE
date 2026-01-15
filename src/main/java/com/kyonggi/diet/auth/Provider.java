package com.kyonggi.diet.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    KAKAO("카카오"),
    GOOGLE("구글"),
    APPLE("애플");

    private final String name;
}
