package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "음식 DTO")
public class DietFoodDTO {

    @Schema(description = "음식 ID")
    private Long id;

    @Schema(description = "음식 이름")
    private String name;

    @Schema(description = "음식 타입")
    private DietFoodType type;
}
