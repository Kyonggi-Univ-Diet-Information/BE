package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DetailedMenu {
    /** ---------- 밥류 ---------- */
    RICE_BOWL("덮밥"), // 덮밥류
    CURRY("카레"), // 덮밥류
    BIBIMBAP("비빔밥"), // 비빔밥류
    FRIED_RICE("볶음밥"), // 볶음밥류

    /** ---------- 찌개/탕류 ---------- */
    STEW("찌개"),
    SOUP("탕"),
    NABE("나베"),
    JJAGEULI("짜글이"),
    JJIM("찜"),

    /** ---------- 면류 ---------- */
    RAMEN("라면"),
    UDON("우동"),
    COLD_NOODLE("냉면"),
    JJAMPPONG("짬뽕"),
    JJAJANGMYEON("짜장면"),
    SPICY_NOODLE("얼큰면"),
    SOMEN("모밀"),
    PHO("쌀국수"),
    PASTA("파스타"),

    /** ---------- 고기류 ---------- */
    STEAK("스테이크"),

    /** ---------- 분식류 ---------- */
    TTEOKBOKKI("떡볶이"),
    GIMBAP("김밥"),

    /** ---------- 튀김류 ---------- */
    PORK_CUTLET("돈까스"),
    CHICKEN_CUTLET("치킨까스"),
    MANDU("만두"),
    CHICKEN("치킨"),
    FRIED("튀김"),
    GUOUI("꿔이"),
    SPRING_ROLL("춘권"),
    JJAJO("짜조"),

    /** ---------- 버거류 ---------- */
    BURGER("버거"),
    TACO("타코"),
    SANDWICH("샌드위치"),

    /** ---------- 커피류 ---------- */
    AMERICANO("아메리카노"),
    LATTE("라떼"),

    /** ---------- 논커피류 ---------- */
    SMOOTHIE("스무디"),
    TEA("티"),
    ADE("에이드"),
    // LATTE("라떼") 커피류가 아닌 라떼 종류

    /** ---------- 기타 ---------- */
    ETC("기타");

    private final String koreanName;

    public static DetailedMenu fromKoreanName(String name) {
        return Arrays.stream(values())
                .filter(d -> d.koreanName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown detailed menu: " + name));
    }
}
