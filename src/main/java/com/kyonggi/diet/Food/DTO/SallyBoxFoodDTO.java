package com.kyonggi.diet.Food.DTO;

import com.kyonggi.diet.Food.eumer.ESquareCategory;
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

    @Schema(description = "샐리박스 음식 카테고리")
    private SallyBoxCategory category;

    @Schema(description = "샐리박스 음식 카테고리 한국어")
    private String categoryKorean;
}
