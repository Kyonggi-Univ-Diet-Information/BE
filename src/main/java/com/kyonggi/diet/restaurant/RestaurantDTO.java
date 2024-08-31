package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.Review;
import lombok.*;

import java.util.List;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private List<Review> reviews;
    private RestaurantType restaurantType;
}
