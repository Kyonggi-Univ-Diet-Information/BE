package com.kyonggi.diet.review.favoriteReview.service;


import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;

import java.util.List;

public interface FavoriteDietFoodReviewService {

    /**
     * 관심 음식 리뷰 저장 메서드
     * @param favoriteDietFoodReview (FavoriteDietFoodReview)
     */
    void save(FavoriteDietFoodReview favoriteDietFoodReview);

    /**
     * 관심 음식 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param memberId (Long)
     */
    void createFavoriteDietFoodReview(Long reviewId, Long memberId);

    /**
     * 관심 음식 리뷰 엔티티 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReview
     */
    FavoriteDietFoodReview findOne(Long id);

    /**
     * 관심 음식 리뷰 DTO 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReviewDTO
     */
    FavoriteDietFoodReviewDTO findById(Long id);

    /**
     * 관심 음식 리뷰 DTO 전체 조회 메서드
     * @return List<FavoriteDietFoodReviewDTO>
     */
    List<FavoriteDietFoodReviewDTO> findAll();

    /**
     * 멤버별 관슴 음식 리뷰 DTO 조회 메서드
     * @param memberId (Long)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    List<FavoriteDietFoodReviewDTO> findFavoriteDietFoodReviewListByMember(Long memberId);
}
