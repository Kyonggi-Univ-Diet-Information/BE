package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.Cuisine;
import com.kyonggi.diet.Food.eumer.DetailedMenu;
import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.eumer.SallyBoxCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Schema(description = "샐리박스 음식 DTO")
public class SallyBoxFoodDTO {

    @Schema(description = "샐리박스 음식 ID")
    private Long id;

    @Schema(description = "샐리박스 음식 이름")
    private String name;

    @Schema(description = "샐리박스 음식 영어 이름")
    private String nameEn;

    @Schema(description = "샐리박스 음식 가격")
    private Long price;

    @Schema(description = "요리 방식")
    private Cuisine cuisine;

    @Schema(description = "음식 종류")
    private FoodType foodType;

    @Schema(description = "세부 메뉴")
    private DetailedMenu detailedMenu;

    @Schema(description = "리뷰 수")
    private Long reviewCount;

    /** /////////// */

    @Schema(description = "샐리박스 음식 카테고리")
    private SallyBoxCategory category;

    @Schema(description = "샐리박스 음식 카테고리 한국어")
    private String categoryKorean;
}
