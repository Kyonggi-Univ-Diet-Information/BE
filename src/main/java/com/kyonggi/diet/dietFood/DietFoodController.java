package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.dietFood.service.DietFoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/diet-food")
@Slf4j
@Tag(name = "음식 API", description = "음식 생성에 대한 API 입니다. 현재는 사용되지 않습니다.")
public class DietFoodController {

    private final DietFoodService dietFoodService;

    @PostMapping("/new")
    public ResponseEntity<String> createDietFood(@RequestBody DietFoodDTO dietFoodDTO) {
        if(dietFoodService.checkExistByName(dietFoodDTO.getName()))
            return ResponseEntity.ok("Already exist dietFood");
        dietFoodService.save(dietFoodDTO);
        return ResponseEntity.ok("Successfully create");
    }
}
