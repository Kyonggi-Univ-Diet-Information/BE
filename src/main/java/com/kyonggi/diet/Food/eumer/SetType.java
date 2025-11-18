package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SetType {
    SET("세트"),
    COMBO("콤보");

    private final String koreanName;
}
