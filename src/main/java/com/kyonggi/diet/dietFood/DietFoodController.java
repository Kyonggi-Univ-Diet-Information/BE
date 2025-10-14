package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.controllerDocs.DietFoodControllerDocs;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dietFood")
@Tag(name = "기숙사 식당 음식 API", description = "기숙사 식당 음식에 대한 API 입니다. (조회, 생성, 삭제, 수정)")
public class DietFoodController implements DietFoodControllerDocs {
    private final DietFoodService dietFoodService;

    /**
     * 음식별 이름 조회(한국어, 영어)
     * @param id (Long)
     */
    @GetMapping("/get-names/{id}")
    public ResponseEntity<FoodNamesDTO> getNamesByFoodId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dietFoodService.findNamesByFoodId(id));
        } catch (NoSuchElementException e) {
            FoodNamesDTO foodNamesDTO = new FoodNamesDTO();
            return ResponseEntity.ok(foodNamesDTO);
        }
    }
}
