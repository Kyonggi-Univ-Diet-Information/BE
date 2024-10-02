package com.kyonggi.diet.diet;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDTO {

    private Long id;
    private DietContentDTO dietContentDTO;
    private DietFoodDTO dietFoodDTO;
}
