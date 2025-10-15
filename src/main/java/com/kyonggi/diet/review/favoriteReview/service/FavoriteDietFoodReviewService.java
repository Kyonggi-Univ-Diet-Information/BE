package com.kyonggi.diet.review.favoriteReview.service;


import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
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
     * @param email (String)
     */
    void createFavoriteDietFoodReview(Long reviewId, String email);

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
     * 멤버별 관심 음식 리뷰 DTO 조회 메서드
     * @param email (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    List<FavoriteDietFoodReviewDTO> findFavoriteDietFoodReviewListByMember(String email);

    /**
     * 멤버별 관심 음식 리뷰 삭제 메서드
     * @param email (String)
     * @param reviewId (Long)
     */
    void deleteFavoriteReview(String email, Long reviewId);

    /**
     * 해당 리뷰에 대한 좋아요 수 카운트 추출
     * @param reviewId (Long)
     * @return Long
     */
    public Long getFavoriteReviewCountByReviewId(Long reviewId);

    /**
     * 인기 TOP 5 리뷰 DTO 조회
     * @return List<ForTopReviewDTO>
     */
    List<ForTopReviewDTO> find5DietFoodReviewsBest();
}
