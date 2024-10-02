package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.domain.RestaurantReview;
import lombok.*;

import java.util.List;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private List<RestaurantReview> restaurantReviews;
    private RestaurantType restaurantType;
}
