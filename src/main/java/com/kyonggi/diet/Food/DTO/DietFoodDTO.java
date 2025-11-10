package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.DietFoodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    @Schema(description = "음식 이름(영어)")
    private String nameEn;

    @Schema(description = "음식 타입")
    private DietFoodType type;

    @Schema(description = "리뷰 수")
    private Long reviewCount;
}
