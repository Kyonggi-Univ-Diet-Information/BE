package com.kyonggi.diet.review.image.repository;

import com.kyonggi.diet.review.image.domain.SallyBoxFoodReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SallyBoxFoodReviewImageRepository extends JpaRepository<SallyBoxFoodReviewImage, Long> {
    List<SallyBoxFoodReviewImage> findAllByReview_IdOrderBySortOrderAsc(Long reviewId);

    void deleteAllByReview_Id(Long reviewId);
}
