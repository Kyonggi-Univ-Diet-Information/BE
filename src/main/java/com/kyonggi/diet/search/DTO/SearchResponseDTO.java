package com.kyonggi.diet.search.DTO;

import com.kyonggi.diet.Food.eumer.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "검색 응답 DTO")
public class SearchResponseDTO {

    @Schema(description = "음식 ID")
    private Long menuId;

    @Schema(description = "가격")
    private Long price;

    @Schema(description = "식당 타입")
    private RestaurantType restaurantType;

    @Schema(description = "서브 식당 (경슐랭 전용)", nullable = true)
    private SubRestaurant subRestaurant;

    @Schema(description = "한글 이름")
    private String name;

    @Schema(description = "영문 이름")
    private String nameEn;

    @Schema(description = "요리 방식")
    private Cuisine cuisine;

    @Schema(description = "음식 종류")
    private FoodType foodType;

    @Schema(description = "세부 메뉴")
    private DetailedMenu detailedMenu;

    @Schema(description = "리뷰 개수")
    private Long reviewCount;

    @Schema(description = "평균 평점 (리뷰 없으면 null)")
    private Double averageRating;
}
