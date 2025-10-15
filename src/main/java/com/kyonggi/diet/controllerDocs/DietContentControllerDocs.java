package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietTime;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.util.Map;

public interface DietContentControllerDocs {

    @Operation(summary = "이번주 식단 조회", description = "이번주의 기숙사 식당 식단을 조회하는 API")
    Map<String, Map<DayOfWeek, Map<DietTime, DietContentDTO>>> dormitoryHome();

    @Operation(
            summary = "특정 요일의 식단 조회",
            description = """
                        이번 주 내에서 특정 요일에 해당하는 기숙사 식단을 조회.
                        요일(DayOfWeek)은 `MONDAY`, `TUESDAY`, ..., `SUNDAY`
                    """
    )
    ResponseEntity<?> dormitoryDow(DayOfWeek dow);
}
