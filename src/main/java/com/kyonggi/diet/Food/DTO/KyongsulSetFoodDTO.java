package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KyongsulSetFoodDTO {

    private Long id;

    @Schema(description = "단품 음식 ID")
    private Long baseFoodId;

    @Schema(description = "세트 or 콤보")
    private SetType setType;

    @Schema(description = "경슐랭 음식 서브 식당")
    private SubRestaurant subRestaurant;

    @Schema(description = "경슐랭 음식 이름")
    private String name;

    @Schema(description = "경슐랭 음식 이름(영어)")
    private String nameEn;

    @Schema(description = "경슐랭 음식 가격")
    private Long price;

    /**
     * ///////////
     */

    @Schema(description = "경슐랭 음식 카테고리(영어)")
    private KyongsulCategory category;

    @Schema(description = "경슐랭 음식 카테고리(한국어)")
    private String categoryKorean;
}
