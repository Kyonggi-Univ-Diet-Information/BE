package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "식단 DTO")
public class DietDTO {

    @Schema(description = "식단 ID")
    private Long id;

    @Schema(description = "식단 내용 DTO")
    private DietContentDTO dietContentDTO;

    @Schema(description = "음식 DTO")
    private DietFoodDTO dietFoodDTO;
}
