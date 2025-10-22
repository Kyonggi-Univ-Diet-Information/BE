package com.kyonggi.diet.Food.eumer;

import lombok.Getter;

import java.util.List;

@Getter
public enum SubRestaurant {

    MANKWON("만권화밥"),
    SYONG("숑숑돈까스"),
    BURGER_TACO("버거&타코"),
    WIDELGA("위델가"),
    SINMEOI("신머이쌀국수");

    private final String koreanName;

    SubRestaurant(String koreanName) {
        this.koreanName = koreanName;
    }
}
