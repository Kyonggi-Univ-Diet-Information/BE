package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.diet.DTO.DietDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietContentDTO {

    private Long id;
    private DietDTO dietDTO;
    private DietFoodDTO dietFoodDTO;
}
