package com.kyonggi.diet.review.DTO;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecentReviewDTO {
    private Long foodId;
    private Long reviewId;
    private RestaurantType restaurantType;
    private String memberName;
    private double rating;
    private String title;
    private String content;
    private String createdAt;
}