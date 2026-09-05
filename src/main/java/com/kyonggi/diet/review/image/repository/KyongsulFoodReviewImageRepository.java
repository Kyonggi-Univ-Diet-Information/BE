package com.kyonggi.diet.review.image.repository;

import com.kyonggi.diet.review.image.domain.KyongsulFoodReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KyongsulFoodReviewImageRepository extends JpaRepository<KyongsulFoodReviewImage, Long> {
    List<KyongsulFoodReviewImage> findAllByReview_IdOrderBySortOrderAsc(Long reviewId);

    void deleteAllByReview_Id(Long reviewId);
}
