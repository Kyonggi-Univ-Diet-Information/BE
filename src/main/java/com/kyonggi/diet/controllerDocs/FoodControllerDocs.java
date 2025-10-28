package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface FoodControllerDocs {

    // ---------------------- 전체 음식 조회 ----------------------
    @Operation(
            summary = "전체 음식 조회",
            description = "RestaurantType 파라미터에 따라 해당 식당의 전체 음식을 조회합니다."
    )
    ResponseEntity<?> findAll(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type
    );

    // ---------------------- 특정 ID 음식 조회 ----------------------
    @Operation(
            summary = "특정 ID 음식 조회",
            description = "RestaurantType 파라미터에 따라 해당 식당의 특정 음식 ID를 조회합니다."
    )
    ResponseEntity<?> findById(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type,
            @Parameter(name = "id", description = "음식 ID", in = ParameterIn.PATH)
            Long id
    );

    // ---------------------- 서브 식당별 경슐랭 음식 조회 ----------------------
    @Operation(
            summary = "서브 식당별 경슐랭 음식 조회(경슐랭)",
            description = "SubRestaurant 값으로 해당 경슐랭 식당 음식 목록을 조회합니다."
    )
    ResponseEntity<?> findBySubRestaurant(
            @Parameter(name = "subRestaurant", description = "서브 식당 이름", in = ParameterIn.PATH)
            SubRestaurant subRestaurant
    );

    // ---------------------- 음식 이름 조회 ----------------------
    @Operation(
            summary = "음식 이름 조회(한국어 / 영어)",
            description = "음식 ID를 통해 한국어 / 영어 이름을 조회합니다."
    )
    ResponseEntity<?> getNamesByFoodId(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type,
            @Parameter(name = "id", description = "음식 ID", in = ParameterIn.PATH)
            Long id
    );

    //----------------------- 카테코리별 조회----------------------
    /**
     * 카테고리별 음식 조회 (경슐, 이퀘, 샐박)
     */
    @Operation(
        summary = "카테고리별 음식 조회(경슐, 이퀘, 샐박)",
        description = """
            경슐랭(KYONGSUL) 또는 이스퀘어(E_SQUARE) 또는 샐리박스(SALLY_BOX)의 전체 음식 목록을
            카테고리별로 묶어서 반환합니다.<br>
            - KYONGSUL: SubRestaurant → KyongsulCategory → 음식 리스트<br>
            - E_SQUARE: ESquareCategory → 음식 리스트<br><br>
            - SALLY_BOX: SallyBoxCategory → 음식 리스트<br><br>
            """
    )
    ResponseEntity<?> getFoodByCategory(@Parameter(
            name = "type",
            description = "식당 타입 (KYONGSUL: 경슐랭 / E_SQUARE: 이스퀘어 / SALLY_BOX: 샐리박스)",
            required = true ,in = ParameterIn.PATH
        ) RestaurantType type);

}
