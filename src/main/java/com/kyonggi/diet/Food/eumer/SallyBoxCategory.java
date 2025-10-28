package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SallyBoxCategory {
    SANDWICH("샌드위치"),
    BURGER("수제버거"),
    PASTA("파스타"),
    DRINK("커피/음료"),
    RICE("밥");

    private final String koreanName;

    public static SallyBoxCategory valueOfKoreanName(String koreanName) {
        return Arrays.stream(values())
                .filter(value -> value.koreanName.equals(koreanName))
                .findAny()
                .orElse(null);
    }
}
