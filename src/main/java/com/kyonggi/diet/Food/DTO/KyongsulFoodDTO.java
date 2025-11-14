package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "경슐랭 음식 DTO")
public class KyongsulFoodDTO {

    @Schema(description = "경슐랭 음식 ID")
    private Long id;

    @Schema(description = "경슐랭 음식 이름")
    private String name;

    @Schema(description = "경슐랭 음식 이름(영어)")
    private String nameEn;

    @Schema(description = "경슐랭 음식 가격")
    private Long price;

    @Schema(description = "요리 방식")
    private Cuisine cuisine;

    @Schema(description = "음식 종류")
    private FoodType foodType;

    @Schema(description = "세부 메뉴")
    private DetailedMenu detailedMenu;

    @Schema(description = "경슐랭 음식 서브 식당")
    private SubRestaurant subRestaurant;

    @Schema(description = "리뷰 수")
    private Long reviewCount;

    /** /////////// */

    @Schema(description = "경슐랭 음식 카테고리(영어)")
    private KyongsulCategory category;

    @Schema(description = "경슐랭 음식 카테고리(한국어)")
    private String categoryKorean;
}
