package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.NoSuchElementException;

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

    // ---------------------- 카테고리별 음식 조회 ----------------------
    @Operation(
            summary = "카테고리별 음식 조회",
            description = "경슐랭, 이스퀘어, 샐리박스 식당에서 카테고리별로 음식 목록을 조회합니다."
    )
    ResponseEntity<?> getFoodByCategory(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type
    );

    // ---------------------- 요리 방식별 음식 조회 ----------------------
    @Operation(
            summary = "요리 방식별 음식 조회 (Cuisine)",
            description = "식당 종류에 따라 요리 방식(Cuisine)별 음식 목록을 조회합니다."
    )
    ResponseEntity<?> getFoodByCuisine(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type
    );

    // ---------------------- 음식 종류별 음식 조회 ----------------------
    @Operation(
            summary = "음식 종류별 음식 조회 (FoodType)",
            description = "식당 종류에 따라 음식 종류(FoodType)별 음식 목록을 조회합니다."
    )
    ResponseEntity<?> getFoodByFoodType(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type
    );

    // ---------------------- 세부 메뉴별 음식 조회 ----------------------
    @Operation(
            summary = "세부 메뉴별 음식 조회 (DetailedMenu)",
            description = "식당 종류에 따라 세부 메뉴(DetailedMenu)별 음식 목록을 조회합니다."
    )
    ResponseEntity<?> getFoodByDetailedMenu(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            RestaurantType type
    );

    // ---------------------- 리뷰가 가장 많은 음식 Top5 조회 ----------------------
    @Operation(
            summary = "리뷰가 가장 많은 음식 Top5 조회",
            description = "경슐랭, 이스퀘어, 샐리박스 전체 식당을 통합하여 리뷰 개수가 가장 많은 음식 5개를 반환합니다. 경슐랭 이외의 조회 경우 SubRestaurant는 null값"
    )
    ResponseEntity<?> getTop5Food();

    // ---------------------- 세트/콤보 테이블 내 음식 조회 -------------------------
    @Operation(
            summary = "세트/콤보 테이블 내 음식 조회",
            description = "세트/콤보 테이블 내에 존재하는 음식의 id 값으로 음식에 대한 정보를 조회합니다."
    )
    ResponseEntity<?> getSetsFoodById(
            @Parameter(
                    name = "type",
                    description = "식당 종류 (현재: KYONGSUL만 지원)",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable RestaurantType type,

            @Parameter(
                    name = "foodId",
                    description = "조회할 세트/콤보 음식 ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable Long foodId
    );


    @Operation(
            summary = "단품의 하위 메뉴(세트/콤보) 조회",
            description = """
                    **🚨 현재 경슐랭 식당만 활성화된 상태입니다.**  
                    - 해당 음식 id에 대한 하위 메뉴가 없을 경우 빈 리스트 반환  
                    - 존재할 경우 세트/콤보 정보 목록 반환
                    """
    )
    ResponseEntity<?> findSetsByBaseFoodId(
            @Parameter(
                    name = "type",
                    description = "식당 종류 (현재: KYONGSUL만 지원)",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable RestaurantType type,

            @Parameter(
                    name = "baseFoodId",
                    description = "단품 음식 ID (이 단품을 기준으로 세트/콤보를 조회)",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable Long baseFoodId
    );
}
