package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.dietContent.DTO.CreateNewDietForm;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.dietFood.DietFoodType;
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
public class DietContentController {

    private final DietContentService dietContentService;

    @GetMapping("/dormitory")
    public Map<DayOfWeek, Map<DietTime, DietContentDTO>> dormitoryHome() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        //이번주 식단 가져오기
        List<DietContentDTO> diets = dietContentService.findDietContentsBetweenDates(startOfWeek, endOfWeek);

        //이번주 식단 반환
        Map<DayOfWeek, Map<DietTime, DietContentDTO>> dietMap = new HashMap<>();
        for (DietContentDTO diet : diets) {
            LocalDate localDate = LocalDate.parse(diet.getDate());
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            //해당 요일에 map 없으면 생성
            dietMap.putIfAbsent(dayOfWeek, new HashMap<>());

            dietMap.get(dayOfWeek).put(diet.getTime(), diet);
        }
        return dietMap;
    }
}
