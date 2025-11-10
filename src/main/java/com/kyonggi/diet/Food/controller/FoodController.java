package com.kyonggi.diet.Food.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.Food.service.DietFoodService;
import com.kyonggi.diet.Food.service.ESquareFoodService;
import com.kyonggi.diet.Food.service.KyongsulFoodService;
import com.kyonggi.diet.Food.service.SallyBoxFoodService;
import com.kyonggi.diet.controllerDocs.FoodControllerDocs;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
@Tag(name = "음식 API", description = "경슐랭 / 기숙사 / 이스퀘어 / 샐리박스 식당 음식 통합 API (조회, 생성, 삭제, 수정)")
public class FoodController implements FoodControllerDocs {

    private final KyongsulFoodService kyongsulFoodService;
    private final DietFoodService dietFoodService;
    private final ESquareFoodService esquareFoodService;
    private final SallyBoxFoodService sallyBoxFoodService;

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
                case E_SQUARE -> ResponseEntity.ok(esquareFoodService.findAll());
                case SALLY_BOX -> ResponseEntity.ok(sallyBoxFoodService.findAll());
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
                case E_SQUARE -> ResponseEntity.ok(esquareFoodService.findById(id));
                case SALLY_BOX -> ResponseEntity.ok(sallyBoxFoodService.findById(id));
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
                case E_SQUARE -> ResponseEntity.ok(esquareFoodService.findNamesByFoodId(id));
                case SALLY_BOX -> ResponseEntity.ok(sallyBoxFoodService.findNamesByFoodId(id));
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type: " + type);
            };
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    /**
     * 카테고리별 음식 조회 (경슐, 이퀘, 샐박)
     */
    @GetMapping("/{type}/each-category")
    public ResponseEntity<?> getFoodByCategory(@PathVariable RestaurantType type) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findFoodByCategory();
                case E_SQUARE -> esquareFoodService.findFoodByCategory();
                case SALLY_BOX -> sallyBoxFoodService.findFoodByCategory();
                default -> null;
            };

            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("Invalid restaurant type: " + type);
            }

            Map<String, Object> response = Map.of("result", result);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{type}/top5-menu")
    public ResponseEntity<?> getTop5FoodByRestaurantType(@PathVariable RestaurantType type) {
        try {
            Object result = switch (type) {
                case DORMITORY -> dietFoodService.getFavoriteTop5Foods();
                case KYONGSUL -> kyongsulFoodService.getFavoriteTop5Foods();
                case E_SQUARE -> esquareFoodService.getFavoriteTop5Foods();
                case SALLY_BOX -> sallyBoxFoodService.getFavoriteTop5Foods();
                default -> null;
            };

            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type: " + type);
            }

            Map<String, Object> response = Map.of("result", result);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
