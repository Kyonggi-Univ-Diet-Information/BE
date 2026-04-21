package com.kyonggi.diet.dietContent;

import com.kyonggi.diet.controllerDocs.DietContentControllerDocs;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DTO.SingleDayDietResponse;
import com.kyonggi.diet.dietContent.service.DietContentService;
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

    @GetMapping("/dormitory/dow/{dow}")
    public ResponseEntity<?> dormitoryDow(@PathVariable("dow") DayOfWeek dow) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        LocalDate targetDate = startOfWeek.plusDays(dow.getValue() % 7);
        String targetDateStr = targetDate.toString();

        Map<String, Object> result = new HashMap<>();
        try {
            List<DietContentDTO> diets = dietContentService.findDietContentsAtDay(dow, startOfWeek, endOfWeek);

            Map<DietTime, DietContentDTO> dietMap = new HashMap<>();
            for (DietContentDTO diet : diets) {
                dietMap.put(diet.getTime(), diet);
            }

            for (DietTime time : DietTime.values()) {
                if (!dietMap.containsKey(time)) {
                    dietMap.put(time, DietContentDTO.builder()
                            .date(targetDateStr)
                            .time(time)
                            .status(DietStatus.CLOSED)
                            .contents(new ArrayList<>())
                            .build());
                }
            }

            result.put("result", new SingleDayDietResponse(dow, dietMap));
            return ResponseEntity.ok(result);

        } catch (EntityNotFoundException e) {
            Map<DietTime, DietContentDTO> emptyMap = new HashMap<>();
            for (DietTime time : DietTime.values()) {
                emptyMap.put(time, DietContentDTO.builder()
                        .date(targetDateStr)
                        .time(time)
                        .status(DietStatus.NO_DATA)
                        .contents(new ArrayList<>())
                        .build());
            }
            result.put("result", new SingleDayDietResponse(dow, emptyMap));
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/dormitory")
    public Map<String, Map<DayOfWeek, Map<DietTime, DietContentDTO>>> dormitoryHome() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

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
