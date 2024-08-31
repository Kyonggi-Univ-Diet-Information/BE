package com.kyonggi.diet.diet.DTO;

import com.kyonggi.diet.diet.DietTime;
import com.kyonggi.diet.dietContent.DietContentDTO;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.review.Review;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDTO {

    private Long id;
    private String date;
    private DietTime time;
    private List<DietContentDTO> contents;

}
