package com.kyonggi.diet.dietContent.DTO;

import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietContent.DietTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "식단 내용 DTO")
public class DietContentDTO {

    @Schema(description = "식단 내용 ID")
    private Long id;

    @Schema(description = "식단 날짜")
    private String date;

    @Schema(description = "식단 시간[BREAKFAST, LUNCH, DINNER")
    private DietTime time; // [BREAKFAST, LUNCH, DINNER]

    @Schema(description = "식단 DTO 리스트")
    private List<DietDTO> contents;

}
