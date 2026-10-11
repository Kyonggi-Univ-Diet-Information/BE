package com.kyonggi.diet.review.image.repository;

import com.kyonggi.diet.review.image.domain.ESquareFoodReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ESquareFoodReviewImageRepository extends JpaRepository<ESquareFoodReviewImage, Long> {
    List<ESquareFoodReviewImage> findAllByReview_IdOrderBySortOrderAsc(Long reviewId);

    void deleteAllByReview_Id(Long reviewId);
}
