package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.kyongsul.SubRestaurant;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface KyongsulFoodControllerDocs {

    /**
     * 전체 음식 조회 API
     */
    @Operation(
        summary = "경슐랭 전체 음식 조회",
        description = "경슐랭에 등록된 모든 음식 데이터를 조회합니다."
    )
    ResponseEntity<?> findAll();

    /**
     * 해당 id에 대한 음식 조회
     */
    @Operation(
        summary = "경슐랭 음식 단건 조회",
        description = "음식 ID를 기준으로 경슐랭 음식 데이터를 조회합니다."
    )
    ResponseEntity<?> getDietFoodById(
        @Parameter(description = "조회할 음식 ID", example = "1")
        @PathVariable Long id
    );

    /**
     * 식당 종류에 따른 음식 조회
     */
    @Operation(
        summary = "식당 구분별 음식 조회",
        description = "서브 식당 구분(SubRestaurant)에 따라 해당 식당에서 제공되는 음식 리스트를 조회합니다."
    )
    ResponseEntity<?> getDietFoodBySubRestaurant(
        @Parameter(description = "식당 분류 (예: MANKWON, SYONG, BURGER_TACO, WIDELGA, SINMEOI", example = "STUDENT")
        @PathVariable SubRestaurant subRestaurant
    );

    /**
     * 음식 id로 한국, 영어 이름 구하기
     * @param id (Long)
     */
    @Operation(
            summary = "경슐 음식별 한국, 영어 이름 조회",
            description = "경슐 음식별 한국, 영어 이름 조회"
    )
    @GetMapping("/get-names/{id}")
    @Parameter(name = "id", description = "경슐 음식 ID")
    ResponseEntity<FoodNamesDTO> getNamesByFoodId(@PathVariable Long id);
}
