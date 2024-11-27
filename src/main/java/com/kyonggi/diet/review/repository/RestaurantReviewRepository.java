package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.review.domain.RestaurantReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RestaurantReviewRepository extends JpaRepository<RestaurantReview, Long> {
    @Query("select r from RestaurantReview r where r.restaurant = :restaurant")
    List<RestaurantReview> findReviewsByRestaurant(@Param("restaurant") Restaurant restaurant);

    @Query("select avg(r.rating) from RestaurantReview r where r.restaurant = :restaurant")
    Double findAverageRatingByRestaurant(@Param("restaurant") Restaurant restaurant);
}
