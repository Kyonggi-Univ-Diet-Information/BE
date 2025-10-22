package com.kyonggi.diet.Food.eumer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "식당 종류")
public enum RestaurantType {
    @Schema(description = "기숙사 식당")
    DORMITORY("기숙사 식당"),

    @Schema(description = "이스퀘어 식당")
    E_SQUARE("이스퀘어"),

    @Schema(description = "감성코어 식당")
    EMOTIONAL_CORE("감성코어"),

    @Schema(description = "경슐랭 식당")
    KYONGSUL("경슐랭");

    private final String name;
    RestaurantType(String name) { this.name = name; }
}
