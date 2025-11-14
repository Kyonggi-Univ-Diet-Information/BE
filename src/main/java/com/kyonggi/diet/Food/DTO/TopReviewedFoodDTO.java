package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "관심 많은 음식 5개")
public class TopReviewedFoodDTO {

    @Schema(description = "음식 id")
    private Long id;

    @Schema(description = "음식이 속한 식당")
    private RestaurantType restaurantType;

    @Schema(description = "음식 이름")
    private String name;

    @Schema(description = "음식 영어 이름")
    private String nameEn;

    @Schema(description = "리뷰 개수")
    private Long reviewCount;

    @Schema(description = "가격")
    private Long price;

    @Schema(description = "요리 방법")
    private Cuisine cuisine;

    @Schema(description = "음식 종류")
    private FoodType foodType;

    @Schema(description = "세부 메뉴")
    private DetailedMenu detailedMenu;

    @Schema(description = "경슐랭의 메뉴일 경우 서브 식당. 나머지는 null")
    private SubRestaurant subRestaurant; // Kyongsul만 해당, 나머지는 null
}