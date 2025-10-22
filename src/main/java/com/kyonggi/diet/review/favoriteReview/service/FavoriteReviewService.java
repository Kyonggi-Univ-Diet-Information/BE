package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;

/**
 * 리뷰 좋아요 공통 인터페이스
 * - RestaurantType별 FavoriteReviewService 구현체 자동 매핑용
 */
public interface FavoriteReviewService {

    /** 식당 타입 반환 */
    RestaurantType getRestaurantType();

    /** 좋아요 생성 */
    void createFavoriteReview(Long reviewId, String email);

    /** 좋아요 삭제 */
    void deleteFavoriteReview(String email, Long reviewId);

    /** 좋아요 개수 조회 */
    Long getFavoriteReviewCountByReviewId(Long reviewId);

    /** 멤버별 즐겨찾기 리뷰 리스트 조회 */
    Object findFavoriteReviewListByMember(String email);

    /** 한개 조회 */
    Object findById(Long id);
}
