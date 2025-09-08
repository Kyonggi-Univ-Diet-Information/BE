package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.controllerDocs.DietContentControllerDocs;
import com.kyonggi.diet.dietContent.DTO.CreateNewDietForm;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.dietFood.DietFoodType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet-content")
@Slf4j
@CrossOrigin("*")
@Tag(name = "식단 내용 API", description = "식단 내용에 대한 API 입니다.")
public class DietContentController implements DietContentControllerDocs {

    private final DietContentService dietContentService;

    @GetMapping("/dormitory")
    public Map<String, Map<DayOfWeek, Map<DietTime, DietContentDTO>>> dormitoryHome() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Map<String, Map<DayOfWeek, Map<DietTime, DietContentDTO>>> result = new HashMap<>();

        try {
            // 이번주 식단 가져오기
            List<DietContentDTO> diets = dietContentService.findDietContentsBetweenDates(startOfWeek, endOfWeek);

            // 이번주 식단 반환
            Map<DayOfWeek, Map<DietTime, DietContentDTO>> dietMap = new HashMap<>();
            for (DietContentDTO diet : diets) {
                LocalDate localDate = LocalDate.parse(diet.getDate());
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();

                // 해당 요일에 map 없으면 생성
                dietMap.putIfAbsent(dayOfWeek, new HashMap<>());

                dietMap.get(dayOfWeek).put(diet.getTime(), diet);
            }
            result.put("result", dietMap);

            return result;
        } catch (EntityNotFoundException e) {
            result.put("result", null);
            return result; //이번주 식단 조회 안될 시 null 반환
        }
    }
}
