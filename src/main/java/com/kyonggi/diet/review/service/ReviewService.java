package com.kyonggi.diet.review.service;

import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.Review;
import com.kyonggi.diet.review.DTO.ReviewDTO;

import java.util.List;

public interface ReviewService {

    /**
     * Review 엔티티 조회
     * @param id (Long)
     * @return Review
     */
    public Review findOne(Long id);

    /**
     * 저장 메서드
     * @param review (Review)
     * @return id (Long)
     */
     public Long saveReview(Review review);

    public void createRestaurantReview(ReviewDTO reviewDTO, RestaurantType type, Long memberId);
    public void createDietFoodReview(ReviewDTO reviewDTO, Long dietFoodId, Long memberId);

     /**
      * Review DTO 조회
      * @param id (Long)
      * @return ReviewDTO
      */
     public ReviewDTO findReview(Long id);

     /**
      * Review DTO 리스트 조회
      * @return List<ReviewDTO>
      */
     public List<ReviewDTO> findAllReview();

     /**
      * 리뷰 수정 메서드
      * @param reviewId (Long)
      * @param reviewDTO (ReviewDTO)
      */
     public void modifyReview(Long reviewId, ReviewDTO reviewDTO);

      /**
       * 리뷰 삭제 메서드
       * @param id (Long)
       */
      public void deleteReview(Long id);

      /**
       * 기숙사 식당 리뷰 DTO 조회
       * @return List<ReviewDTO>
       */
      public List<ReviewDTO> findAllDormitoryReviews();
}
