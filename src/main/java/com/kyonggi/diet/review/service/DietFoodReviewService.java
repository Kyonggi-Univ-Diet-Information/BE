package com.kyonggi.diet.review.service;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

public interface DietFoodReviewService {

    /**
     * 음식 리뷰 생성 메서드
     *
     * @param reviewDTO  (ReviewDTO)
     * @param dietFoodId (Long)
     * @param email      (String)
     */
    public void createDietFoodReview(ReviewDTO reviewDTO, Long dietFoodId, String email);

    /**
     * 음식 Review 엔티티 조회
     *
     * @param id (Long)
     * @return DietFoodReview
     */
    public DietFoodReview findOne(Long id);

    /**
     * 음식 저장 메서드
     *
     * @param dietFoodReview (DietFoodReview)
     * @return id (Long)
     */
    public Long saveReview(DietFoodReview dietFoodReview);

    /**
     * 음식 Review DTO 조회
     *
     * @param id (Long)
     * @return ReviewDTO
     */
    public ReviewDTO findReview(Long id);

    /**
     * 음식 Review DTO 리스트 조회
     *
     * @return List<ReviewDTO>
     */
    public List<ReviewDTO> findAllReview();

    /**
     * 페이징된 ReivewDTO 조회
     * @param pageNo (int)
     * @return Page<ReviewDTO>
     */
    public Page<ReviewDTO> getAllReviewsPaged(int pageNo);

    /**
     * 음식 리뷰 수정 메서드
     *
     * @param reviewId  (Long)
     * @param reviewDTO (ReviewDTO)
     */
    public void modifyReview(Long reviewId, ReviewDTO reviewDTO);

    /**
     * 음식 리뷰 삭제 메서드
     *
     * @param id (Long)
     */
    public void deleteReview(Long id);

    /**
     * 리뷰 작성자가 멤버가 맞는 지 확인
     *
     * @param reviewId (Long)
     * @param email    (String)
     * @return boolean
     */
    boolean verifyMember(Long reviewId, String email);

    /**
     * 음식 id를 통해 리뷰 리스트 구하기
     * @param dietFoodId (Long)
     * @return List<ReviewDTO>
     */
    public List<ReviewDTO> findListById(Long dietFoodId);

    /**
     * 해당 음식 리뷰에 대한 평점 구하기
     * @param dietFoodId (Long)
     * @return Double
     */
    Double findAverageRatingByDietFoodId(Long dietFoodId);
}
