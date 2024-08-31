package com.kyonggi.diet.diet;

import com.kyonggi.diet.diet.DTO.CreateNewDietForm;
import com.kyonggi.diet.diet.DTO.DietDTO;
import com.kyonggi.diet.diet.service.DietService;
import com.kyonggi.diet.diet.service.Impl.DietServiceImpl;
import com.kyonggi.diet.dietContent.DietContentDTO;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.dietFood.service.Impl.DietFoodServiceImpl;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant/dormitory/diet")
@Slf4j
@CrossOrigin("*")
public class DietController {

    private final DietService dietService;
    private final DietFoodService dietFoodService;

    @GetMapping("")
    public Map<String, DietDTO> dormitoryHome() {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            List<DietDTO> diets = dietService.findDietsBetweenDates(startOfWeek, endOfWeek);

            Map<String, DietDTO> dietMap = new HashMap<>();
            for (DietDTO diet : diets) {
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
        List<DietContentDTO> dietContentDTOS = new ArrayList<>();
        if(dietFoodService.findDietFood(form.getRice()) != null) {
            dietContentDTOS.add(DietContentDTO.builder()
                    .dietFoodDTO(dietFoodService.findDietFood(form.getRice())).build());
        }
        for (DietFoodDTO side : dietFoodService.findDietFoodsByIdList(form.getSide())) {
            dietContentDTOS.add(DietContentDTO.builder()
                    .dietFoodDTO(side).build());
        }
        if(dietFoodService.findDietFood(form.getSoup()) != null) {
            dietContentDTOS.add(DietContentDTO.builder()
                    .dietFoodDTO(dietFoodService.findDietFood(form.getSoup())).build());
        }
        for (DietFoodDTO desert : dietFoodService.findDietFoodsByIdList(form.getDesert())) {
            dietContentDTOS.add(DietContentDTO.builder()
                    .dietFoodDTO(desert).build());
        }
        DietDTO dietDTO = DietDTO.builder()
                                .date(form.getDate())
                                        .time(DietTime.LUNCH)
                                                .contents(dietContentDTOS)
                                                        .build();
        for (DietFoodDTO dietFood : dietFoodService.findDietFoodsByIdList(form.getSide())) {
            log.info("하잉: {}",dietFood.getName());
        }
        dietService.save(dietDTO);

        return ResponseEntity.ok("Created Diet!");
    }
}
