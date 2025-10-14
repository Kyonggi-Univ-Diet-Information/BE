package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DietFoodControllerDocs {

    /**
     * 기숙사 음식별 이름 조회(한국어, 영어)
     */
    @Operation(
            summary = "기숙사 음식별 이름 조회(한국어, 영어)",
            description = "기숙사 음식별 이름 조회(한국어, 영어)"
    )
    @Parameter(name = "id", description = "긱사 음식 ID")
    @GetMapping("/get-names/{id}")
    ResponseEntity<FoodNamesDTO> getNamesByFoodId(@PathVariable Long id);
}
