package com.kyonggi.diet.review.DTO;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyReviewDTO {
    private Long reviewId;
    private Long foodId;
    private RestaurantType restaurantType;
    private Double rating;
    private String title;
    private String content;
    private String memberName;
    private String createdAt;
}
