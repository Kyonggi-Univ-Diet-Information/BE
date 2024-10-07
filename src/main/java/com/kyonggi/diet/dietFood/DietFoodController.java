package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.dietFood.service.DietFoodService;
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
