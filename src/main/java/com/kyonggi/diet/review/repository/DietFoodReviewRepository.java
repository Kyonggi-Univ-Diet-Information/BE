package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DietFoodReviewRepository extends JpaRepository<DietFoodReview, Long> {
    @Query("select r from DietFoodReview r where r.dietFood.id = :id")
    List<DietFoodReview> findListById(@Param("id") Long dietFoodId);

    @Query("select avg(r.rating) from DietFoodReview r where r.dietFood.id = :id")
    Double findAverageRatingByDietFoodId(@Param("id") Long id);

    @Query("select r from DietFoodReview r where r.dietFood.id = :dietFoodId")
    Page<DietFoodReview> findAllByDietFoodId(Long dietFoodId, Pageable pageable);
}
