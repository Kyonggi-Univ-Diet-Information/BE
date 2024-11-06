package com.kyonggi.diet.restaurant;

import com.kyonggi.diet.review.domain.RestaurantReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "식당 DTO")
public class RestaurantDTO {

    @Schema(description = "식당 ID")
    private Long id;

    @Schema(description = "식당 이름")
    private String name;

    @Schema(description = "식당 리뷰")
    private List<RestaurantReview> restaurantReviews;

    @Schema(description = "식당 타입")
    private RestaurantType restaurantType;
}
