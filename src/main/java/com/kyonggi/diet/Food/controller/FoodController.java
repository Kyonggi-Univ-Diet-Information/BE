package com.kyonggi.diet.Food.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.TopReviewedFoodDTO;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.Food.repository.FoodRepository;
import com.kyonggi.diet.Food.service.DietFoodService;
import com.kyonggi.diet.Food.service.ESquareFoodService;
import com.kyonggi.diet.Food.service.KyongsulFoodService;
import com.kyonggi.diet.Food.service.SallyBoxFoodService;
import com.kyonggi.diet.controllerDocs.FoodControllerDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final FoodRepository foodRepository;

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

    /**
     * 요리 방법별 음식 조회(Cuisine)
     */
    @GetMapping("/{type}/category/cuisine")
    public ResponseEntity<?> getFoodByCuisine(@PathVariable RestaurantType type) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findFoodByCuisine();
                case E_SQUARE -> esquareFoodService.findFoodByCuisine();
                case SALLY_BOX -> sallyBoxFoodService.findFoodByCuisine();
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

    /**
     * 음식 종류별 음식 조회 (FoodType)
     */
    @GetMapping("/{type}/category/food-type")
    public ResponseEntity<?> getFoodByFoodType(@PathVariable RestaurantType type) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findFoodByFoodType();
                case E_SQUARE -> esquareFoodService.findFoodByFoodType();
                case SALLY_BOX -> sallyBoxFoodService.findFoodByFoodType();
                default -> null;
            };

            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
            }

            Map<String, Object> response = Map.of("result", result);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * 세부 메뉴별 음식 조회 (Detailed-menu)
     */
    @GetMapping("/{type}/category/detailed-menu")
    public ResponseEntity<?> getFoodByDetailedMenu(@PathVariable RestaurantType type) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findFoodByDetailedMenu();
                case E_SQUARE -> esquareFoodService.findFoodByDetailedMenu();
                case SALLY_BOX -> sallyBoxFoodService.findFoodByDetailedMenu();
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

    @GetMapping("/top5-menu")
    public ResponseEntity<?> getTop5Food() {
        try {
            List<TopReviewedFoodDTO> result = foodRepository.findTop5ReviewedFoods();

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "리뷰 데이터가 존재하지 않습니다."));
            }

            return ResponseEntity.ok(Map.of("result", result));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ENUM 매핑 실패", "message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "요청 처리 중 오류 발생", "message", e.getMessage()));
        }
    }

    @GetMapping("/{type}/sets-one/{foodId}")
    public ResponseEntity<?> getSetsFoodById(
            @PathVariable RestaurantType type,
            @PathVariable Long foodId
    ) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findOneSetDTO(foodId);

                default -> null;
            };

            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid restaurant type or no data: " + type);
            }
            return ResponseEntity.ok(Map.of("result", result));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/{type}/get-sets/{baseFoodId}")
    public ResponseEntity<?> findSetsByBaseFoodId(
            @PathVariable RestaurantType type,
            @PathVariable Long baseFoodId
    ) {
        try {
            Object result = switch (type) {
                case KYONGSUL -> kyongsulFoodService.findByBaseFood(baseFoodId);
                default -> null;
            };

            if (result == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
            }

            Map<String, Object> response = Map.of("result", result);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.ok(Map.of("result", List.of()));
        }
    }
}
