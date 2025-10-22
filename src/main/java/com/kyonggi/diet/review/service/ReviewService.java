package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.domain.Review;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 식당 리뷰 서비스 공통 인터페이스
 * 각 식당별 구현체(Diet, Kyongsul 등)는 RestaurantType을 명시해야 함.
 */
public interface ReviewService {

    RestaurantType getRestaurantType(); // 어떤 식당용 서비스인지 구분

    void createReview(ReviewDTO dto, Long foodId, String email);

    ReviewDTO findReviewDTO(Long reviewId);

    Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo);

    void modifyReview(Long reviewId, CreateReviewDTO dto);

    void deleteReview(Long reviewId);

    boolean verifyMember(Long reviewId, String email);

    Double getAverageRating(Long foodId);

    Map<Integer, Long> getCountEachRating(Long foodId);

    int getReviewCount(Long foodId);

    List<ForTopReviewDTO> getRecentTop5();

    List<ForTopReviewDTO> getTop5ByRating();

    List<ReviewDTO> findAllByMember(MemberEntity member);

    List<ReviewDTO> findAllByMemberFavorited(MemberEntity member);

    List<ReviewDTO> getAllReviews(Long id);
}
