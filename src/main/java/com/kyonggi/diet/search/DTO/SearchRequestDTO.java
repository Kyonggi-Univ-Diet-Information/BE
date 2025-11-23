package com.kyonggi.diet.search.DTO;

import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.search.SortingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "검색 요청 DTO")
public class SearchRequestDTO {

    @Schema(description = "검색 키워드 (한글/영문 음식명)")
    private String keyword;

    @Schema(description = "음식 종류 (FoodType enum)")
    private FoodType foodType;

    @Schema(description = "식당 종류 (RestaurantType enum)")
    private RestaurantType restaurantType;

    @Schema(description = "최소 가격")
    private Integer priceMin;

    @Schema(description = "최대 가격")
    private Integer priceMax;

    @Schema(
        description = """
            정렬 옵션 (SortingType enum)
            - BASIC: 정렬 없음 (원본 순서)
            - REVIEW_COUNT_DESC: 리뷰 많은 순
            - RATING_DESC: 평점 높은 순
            - PRICE_ASC: 가격 낮은 순
            - PRICE_DESC: 가격 높은 순
            """
    )
    private SortingType sortingType;
}
