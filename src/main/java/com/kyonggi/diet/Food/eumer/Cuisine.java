package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 요리 방식 (Cuisine)
 */
@Getter
@RequiredArgsConstructor
public enum Cuisine {
    KOREAN("한식"),
    FUSION("퓨전"),
    WESTERN("양식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    SNACK("분식"),
    ASIAN("아시안"),
    BEVERAGE("음료");

    private final String koreanName;

    public static Cuisine fromKoreanName(String name) {
        return Arrays.stream(values())
                .filter(c -> c.koreanName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown cuisine: " + name));
    }
}

