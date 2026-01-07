package com.kyonggi.diet.search;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "검색 식당 타입")
public enum SearchRestaurantType {
    @Schema(description = "전체 식당")
    ALL("전체"),

    @Schema(description = "경슐랭")
    KYONGSUL("경슐랭"),

    @Schema(description = "이스퀘어")
    E_SQUARE("이스퀘어"),

    @Schema(description = "샐리박스")
    SALLY_BOX("샐리박스");

    private final String description;

    SearchRestaurantType(String description) {
        this.description = description;
    }
}
