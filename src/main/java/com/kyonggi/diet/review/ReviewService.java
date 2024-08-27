package com.kyonggi.diet.review;

import com.kyonggi.diet.review.DTO.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 저장
     */
    public Long saveReview(Review review) {
        reviewRepository.save(review);
        return review.getId();
    }

    /**
     * 한개 찾기
     */
    public Review findReview(Long id) {
        return reviewRepository.findOne(id);
    }

    /**
     * 모두 찾기
     */
    public List<Review> findAllReview() {
        return reviewRepository.findAll();
    }

    /**
     * 수정
     */
    @Transactional
    public Review modifyReview(Review review, ReviewDTO reviewDTO) {
        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        saveReview(review);
        return review;
    }

    /**
     * 삭제
     */
    @Transactional
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }

}
