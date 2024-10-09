package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteRestaurantReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;

import java.util.List;

public interface FavoriteRestaurantReviewService {

    /**
     * 관심 식당 리뷰 저장 메서드
     * @param favoriteRestaurantReview (FavoriteRestaurantReview)
     */
    void save(FavoriteRestaurantReview favoriteRestaurantReview);

    /**
     * 관심 식당 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param email (String)
     */
    void createFavoriteRestaurantReview(Long reviewId, String email);

    /**
     * 관심 식당 리뷰 엔티티 조회 메서드
     * @param id (Long)
     * @return FavoriteRestaurantReview
     */
    FavoriteRestaurantReview findOne(Long id);

    /**
     * 관심 식당 리뷰 DTO 조회 메서드
     * @param id (Long)
     * @return FavoriteRestaurantReviewDTO
     */
    FavoriteRestaurantReviewDTO findById(Long id);

    /**
     * 관심 식당 리뷰 DTO 전체 조회 메서드
     * @return List<FavoriteRestaurantReviewDTO>
     */
    List<FavoriteRestaurantReviewDTO> findAll();

    /**
     * 멤버별 관심 식당 리뷰 DTO 조회 메서드
     * @param email (String)
     * @return List<FavoriteRestaurantReviewDTO>
     */
    List<FavoriteRestaurantReviewDTO> findFavoriteRestaurantReviewListByMember(String email);
}
