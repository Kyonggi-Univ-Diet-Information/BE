package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.review.domain.DietFoodReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFoodReviewRepository extends JpaRepository<DietFoodReview, Long> {
}
