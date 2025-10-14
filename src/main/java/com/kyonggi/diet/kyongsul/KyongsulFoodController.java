package com.kyonggi.diet.kyongsul;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.controllerDocs.KyongsulFoodControllerDocs;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dietFood/kyongsul")
@Tag(name = "경슐랭 음식 API", description = "경슐랭 음식에 대한 API 입니다. (조회, 생성, 삭제, 수정)")
public class KyongsulFoodController implements KyongsulFoodControllerDocs {
    private final KyongsulFoodService kyongsulFoodService;

    /**
     * 전체 음식 조회 API
     */
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(kyongsulFoodService.findAll());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found All KyongsulFood");
        }
    }

    /**
     * 해당 id에 대한 음식 조회
     * @param id (Long)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDietFoodById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(kyongsulFoodService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found KyongsulFood");
        }
    }

    /**
     * 식당 종류에 따른 음식 조회
     * @param subRestaurant (SubRestaurant)
     */
    @GetMapping("/restaurant/{subRestaurant}")
    public ResponseEntity<?> getDietFoodBySubRestaurant(@PathVariable SubRestaurant subRestaurant) {
        try {
            return ResponseEntity.ok(kyongsulFoodService.findBySubRestaurant(subRestaurant));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found KyongsulFood By SubRestaurant");
        }
    }

    /**
     * 음식 id로 한국, 영어 이름 구하기
     * @param id (Long)
     */
    @GetMapping("/get-names/{id}")
    public ResponseEntity<FoodNamesDTO> getNamesByFoodId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(kyongsulFoodService.findNamesByFoodId(id));
        } catch (NoSuchElementException e) {
            FoodNamesDTO foodNamesDTO = new FoodNamesDTO();
            return ResponseEntity.ok(foodNamesDTO);
        }
    }
}
