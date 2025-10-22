package com.kyonggi.diet.Food.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.Food.service.DietFoodService;
import com.kyonggi.diet.Food.service.KyongsulFoodService;
import com.kyonggi.diet.controllerDocs.FoodControllerDocs;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
@Tag(name = "음식 API", description = "경슐랭 / 기숙사 식당 음식 통합 API (조회, 생성, 삭제, 수정)")
public class FoodController implements FoodControllerDocs {

    private final KyongsulFoodService kyongsulFoodService;
    private final DietFoodService dietFoodService;

    /**
     * 전체 음식 조회
     * @param type (RestaurantType)
     */
    @GetMapping("/{type}/all")
    public ResponseEntity<?> findAll(@PathVariable RestaurantType type) {
        try {
            return switch (type) {
                case KYONGSUL -> ResponseEntity.ok(kyongsulFoodService.findAll());
                case DORMITORY -> ResponseEntity.ok(dietFoodService.findAll());
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type: " + type);
            };
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Not Found Food List for type: " + type);
        }
    }

    /**
     * 특정 ID로 음식 조회
     * @param type (RestaurantType)
     * @param id (Long)
     */
    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> findById(@PathVariable RestaurantType type, @PathVariable Long id) {
        try {
            return switch (type) {
                case KYONGSUL -> ResponseEntity.ok(kyongsulFoodService.findById(id));
                case DORMITORY -> ResponseEntity.ok(dietFoodService.findById(id));
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type: " + type);
            };
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Not Found Food by ID: " + id);
        }
    }

    /**
     * 서브 식당별 음식 조회 (Kyongsul 전용)
     * @param subRestaurant (SubRestaurant)
     */
    @GetMapping("/KYONGSUL/restaurant/{subRestaurant}")
    public ResponseEntity<?> findBySubRestaurant(@PathVariable SubRestaurant subRestaurant) {
        try {
            return ResponseEntity.ok(kyongsulFoodService.findBySubRestaurant(subRestaurant));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Not Found KyongsulFood By SubRestaurant");
        }
    }

    /**
     * 음식 id로 한국어, 영어 이름 반환
     * @param type (RestaurantType)
     * @param id (Long)
     */
    @GetMapping("/{type}/get-names/{id}")
    public ResponseEntity<?> getNamesByFoodId(
            @PathVariable RestaurantType type,
            @PathVariable Long id
    ) {
        try {
            return switch (type) {
                case KYONGSUL -> ResponseEntity.ok(kyongsulFoodService.findNamesByFoodId(id));
                case DORMITORY -> ResponseEntity.ok(dietFoodService.findNamesByFoodId(id));
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type: " + type);
            };
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
