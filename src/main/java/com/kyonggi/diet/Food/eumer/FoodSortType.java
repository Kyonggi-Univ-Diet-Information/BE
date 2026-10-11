package com.kyonggi.diet.Food.eumer;

public enum FoodSortType {
    RATING, NAME, REVIEW_COUNT;

    /** 잘못되었거나 없는 값이면 null 반환 -> 호출부에서 기본 정렬 유지 */
    public static FoodSortType from(String raw) {
        if (raw == null) return null;
        try {
            return FoodSortType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}