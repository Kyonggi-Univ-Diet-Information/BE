package com.kyonggi.diet.search;

public enum SortingType {
    BASIC("기본순"),
    REVIEW_COUNT_DESC("리뷰 많은 순"),
    RATING_DESC("평점 높은 순"),
    PRICE_ASC("가격 낮은 순"),
    PRICE_DESC("가격 높은 순");

    private final String description;

    SortingType(String description) {
        this.description = description;
    }
}
