package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.review.Review;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietFoodDTO {

    private Long id;
    private String name;
    private DietFoodType type;
    private List<Review> reviews;
}
