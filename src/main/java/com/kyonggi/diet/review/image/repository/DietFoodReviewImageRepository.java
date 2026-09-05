package com.kyonggi.diet.review.image.repository;

import com.kyonggi.diet.review.image.domain.DietFoodReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietFoodReviewImageRepository extends JpaRepository<DietFoodReviewImage, Long> {
    List<DietFoodReviewImage> findAllByReview_IdOrderBySortOrderAsc(Long reviewId);

    void deleteAllByReview_Id(Long reviewId);
}
