package com.kyonggi.diet.dietContent.DTO;

import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietContent.DietTime;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietContentDTO {

    private Long id;
    private String date;
    private DietTime time; // [BREAKFAST, LUNCH, DINNER]
    private List<DietDTO> contents;

}
