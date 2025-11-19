package com.kyonggi.diet.Food.eumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum KyongsulCategory {
    // 공통
    SIDE("사이드"),
    ETC("기타"),

    // 만권화밥
    BOWL("덮밥"),
    STEAK("스테이크"),
    NOODLE("면"),

    // 숑숑돈까스
    CUTLET("돈까스"),
    KATSU("카츠"),
    SOBA("소바"),
    CURRY("커리"),
    PASTA("파스타"),
    UDON("우동"),
    TTEOKBOKKI("떡볶이"), //버거&타코도

    // 버거&타코
    TACO("타코"),
    BURGER("버거"),
    CHICKEN("치킨"),
    PACK("팩"),

    // 위델가
    JJIGAE("찌개"),
    RICE("밥"),
    SOUP("탕"),

    // 신머이쌀국수
    PHO("쌀국수"),
    SHRIMP("새우"),
    BANHMI("반미");

    private final String koreanName;

    public static KyongsulCategory fromKorean(String koreanName) {
        return Arrays.stream(values())
                .filter(c -> c.koreanName.equals(koreanName.trim()))
                .findFirst()
                .orElse(ETC);
    }
}