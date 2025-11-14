package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FoodType {
    RICE_BOWL("덮밥류"),
    BIBIMBAP("비빔밥류"),
    FRIED_RICE("볶음밥류"),
    MEAT("고기류"),
    NOODLE("면류"),
    FRIED("튀김류"),
    SNACK("분식류"),
    BURGER("버거류"),
    SOUP_STEW("찌개/탕류"),
    COFFEE("커피류"),
    NON_COFFEE("논커피류"),
    ETC("기타");

    private final String koreanName;

    public static FoodType fromKoreanName(String name) {
        return Arrays.stream(values())
            .filter(f -> f.koreanName.equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown food type: " + name));
    }
}
