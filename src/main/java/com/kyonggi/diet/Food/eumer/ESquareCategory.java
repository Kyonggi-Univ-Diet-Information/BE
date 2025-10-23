package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ESquareCategory {
    STREET("분식"),
    MEALS("식사"),
    CUTLET("돈까스"),
    COFFEE_BEVERAGE("커피/음료"),
    ICE_CREAM("아이스크림");

    private final String koreanName;

    public static ESquareCategory valueOfKoreanName(String koreanName) {
        return Arrays.stream(values())
                .filter(value -> value.koreanName.equals(koreanName))
                .findAny()
                .orElse(null);
    }
}
