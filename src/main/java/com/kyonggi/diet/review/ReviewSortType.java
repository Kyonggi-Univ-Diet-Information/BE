package com.kyonggi.diet.review;

public enum ReviewSortType {
    RECENT, RATING_DESC, RATING_ASC;

    /** 잘못되었거나 없는 값이면 null 반환 -> 호출부에서 기존 기본 정렬 유지 */
    public static ReviewSortType from(String raw) {
        if (raw == null) return null;
        try {
            return ReviewSortType.valueOf(raw.trim().toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}