package com.kyonggi.diet.review.service;

import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface KyongsulFoodReviewService {

    /**
     * 경슐랭 리뷰 생성 메서드
     * @param reviewDTO (ReviewDTO)
     * @param foodId    (Long)
     * @param email     (String)
     */
    void createKyongsulFoodReview(ReviewDTO reviewDTO, Long foodId, String email);

    /**
     * 경슐랭 음식 리뷰 삭제 메서드
     * @param reviewId (Long)
     */
    void deleteReview(Long reviewId);

    /**
     * 경슐랭 음식 리뷰 수정 메서드
     * @param createReviewDTO (CreateReviewDTO)
     * @param reviewId (Long)
     */
    void modifyKyongsulFoodReview(Long reviewId, CreateReviewDTO createReviewDTO);

    /**
     * 경슐랭 음식 리뷰 DTO 한개 조회 메서드
     * @param reviewId (Long)
     * @return reviewDTO
     */
    ReviewDTO findReviewDTOById(Long reviewId);

    /**
     * 페이징된 ReviewDTO 조회
     * @param pageNo (Int)
     * @return Page<ReviewDTO>
     */
    Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo);

    /**
     * 경슐랭 음식에 대한 평균 평점 구하기 메서드
     * @param foodId (Long)
     * @return averageRating (Double)
     */
    Double getAverageRatingForReview(Long foodId);

    /**
     * 리뷰 작성자가 멤버가 맞는 지 확인
     * @param reviewId (Long)
     * @param email    (String)
     * @return boolean
     */
    boolean verifyMember(Long reviewId, String email);

    /**
     * 해당 음식에 대한 각 리뷰 rating 카운팅 개수 리턴  메서드
     * @param foodId (Long)
     * @return Map<Integer, Long>
     */
    Map<Integer, Long> getCountEachRating(Long foodId);

    /**
     * 경슐랭 음식 각 음식에 대한 리뷰 카운팅
     * @param id (Long)
     */
    int findKyongsulFoodReviewCount(Long id);
}
