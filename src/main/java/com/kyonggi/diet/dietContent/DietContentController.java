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
@RequestMapping("/api/dietContent")
@Slf4j
@CrossOrigin("*")
public class DietContentController {

    private final DietContentService dietContentService;
    private final DietFoodService dietFoodService;

    @GetMapping("/dormitory")
    public Map<String, DietContentDTO> dormitoryHome() {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            List<DietContentDTO> diets = dietContentService.findDietContentsBetweenDates(startOfWeek, endOfWeek);

            Map<String, DietContentDTO> dietMap = new HashMap<>();
            for (DietContentDTO diet : diets) {
                LocalDate localDate = LocalDate.parse(diet.getDate());
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();

                switch (dayOfWeek) {
                    case MONDAY:
                        dietMap.put("monday", diet);
                        break;
                    case TUESDAY:
                        dietMap.put("tuesday", diet);
                        break;
                    case WEDNESDAY:
                        dietMap.put("wednesday", diet);
                        break;
                    case THURSDAY:
                        dietMap.put("thursday", diet);
                        break;
                    case FRIDAY:
                        dietMap.put("friday", diet);
                        break;
                    default:
                        break;
                }
            }
            return dietMap;
    }

    @GetMapping("/new")
    public Map<String, List<DietFoodDTO>> newDiet() {
        Map<String, List<DietFoodDTO>> Diets = new HashMap<>();
        Diets.put("rices", dietFoodService.findDietFoodByType(DietFoodType.RICE));
        Diets.put("sides", dietFoodService.findDietFoodByType(DietFoodType.SIDE));
        Diets.put("soups", dietFoodService.findDietFoodByType(DietFoodType.SOUP));
        Diets.put("deserts", dietFoodService.findDietFoodByType(DietFoodType.DESERT));
        return Diets;
    }

    @PostMapping("/new")
    public ResponseEntity<String> createDiet(@RequestBody CreateNewDietForm form) {
        List<DietFoodDTO> dietFoods = new ArrayList<>();
        List<DietDTO> dietDTOS = new ArrayList<>();
        if(dietFoodService.findDietFood(form.getRice()) != null) {
            dietDTOS.add(DietDTO.builder()
                    .dietFoodDTO(dietFoodService.findDietFood(form.getRice())).build());
        }
        for (DietFoodDTO side : dietFoodService.findDietFoodsByIdList(form.getSide())) {
            dietDTOS.add(DietDTO.builder()
                    .dietFoodDTO(side).build());
        }
        if(dietFoodService.findDietFood(form.getSoup()) != null) {
            dietDTOS.add(DietDTO.builder()
                    .dietFoodDTO(dietFoodService.findDietFood(form.getSoup())).build());
        }
        for (DietFoodDTO desert : dietFoodService.findDietFoodsByIdList(form.getDesert())) {
            dietDTOS.add(DietDTO.builder()
                    .dietFoodDTO(desert).build());
        }
        DietContentDTO dietContentDTO = DietContentDTO.builder()
                                .date(form.getDate())
                                        .time(DietTime.LUNCH)
                                                .contents(dietDTOS)
                                                        .build();
        for (DietFoodDTO dietFood : dietFoodService.findDietFoodsByIdList(form.getSide())) {
            log.info("하잉: {}",dietFood.getName());
        }
        dietContentService.save(dietContentDTO);

        return ResponseEntity.ok("Created Diet!");
    }
}
