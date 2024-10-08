package com.kyonggi.diet.review.service;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import com.kyonggi.diet.review.DTO.ReviewDTO;

import java.util.List;

public interface RestaurantReviewService {

    /**
     * 식당 Review 엔티티 조회
     * @param id (Long)
     * @return Review
     */
    RestaurantReview findOne(Long id);

    /**
     * 식당 리뷰 저장 메서드
     * @param restaurantReview (Review)
     * @return id (Long)
     */
    Long saveReview(RestaurantReview restaurantReview);

    /**
     * 식당 리뷰 생성 메서드
     * @param reviewDTO (ReviewDTO)
     * @param type (RestaurantType_
     * @param email (String)
     */
    void createRestaurantReview(ReviewDTO reviewDTO, RestaurantType type, String email);

     /**
      * 식당 Review DTO 조회
      * @param id (Long)
      * @return ReviewDTO
      */
     ReviewDTO findReview(Long id);

     /**
      * 식당 Review DTO 리스트 조회
      * @return List<ReviewDTO>
      */
     List<ReviewDTO> findAllReview();

     /**
      * 식당 리뷰 수정 메서드
      * @param reviewId (Long)
      * @param reviewDTO (ReviewDTO)
      */
     void modifyReview(Long reviewId, ReviewDTO reviewDTO);

      /**
       * 식당 리뷰 삭제 메서드
       * @param id (Long)
       */
      void deleteReview(Long id);

      /**
       * 리뷰 작성자가 멤버가 맞는 지 확인
       * @param reviewId (Long)
       * @param email    (String)
       * @return boolean
       */
      boolean verifyMember(Long reviewId, String email);

      /**
       * 타입별 식당 리뷰 조회
       * @return List<ReviewDTO>
       */
      List<ReviewDTO> findReviewsByType(RestaurantType type);
}
