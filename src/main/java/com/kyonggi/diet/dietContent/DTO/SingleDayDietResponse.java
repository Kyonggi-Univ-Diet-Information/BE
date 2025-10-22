package com.kyonggi.diet.dietContent.DTO;

import com.kyonggi.diet.dietContent.DietTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@Schema(description = "단일 요일 식단 DTO")
public class SingleDayDietResponse {

    @Schema(description = "요일 (예: MONDAY, TUESDAY 등)")
    private DayOfWeek dayOfWeek;

    @Schema(description = "아침/점심/저녁별 식단 정보")
    private Map<DietTime, DietContentDTO> diet;

    public SingleDayDietResponse(DayOfWeek dayOfWeek, Map<DietTime, DietContentDTO> diet) {
        this.dayOfWeek = dayOfWeek;
        this.diet = diet;
    }
}
