package com.kyonggi.diet.search;

import com.kyonggi.diet.search.DTO.SearchRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "검색 API", description = "경슐랭 / 이스퀘어 / 샐리박스 식당 음식에 대한 검색 API")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("")
    @Operation(
            summary = "통합 음식 검색",
            description = """
                    경슐랭(KYONGSUL), 이스퀘어(E_SQUARE), 샐리박스(SALLY_BOX)를 모두 통합하여 검색합니다. 
                     
                    검색 조건
                    - keyword(한글/영문): 음식 이름 검색  -> 필수 사항
                    - foodType: 음식 타입(FoodType enum)
                        - RICE_BOWL("덮밥류"), BIBIMBAP("비빔밥류"), FRIED_RICE("볶음밥류"),
                        - MEAT("고기류"), NOODLE("면류"), FRIED("튀김류"), SNACK("분식류"), BURGER("버거류"),
                        - SOUP_STEW("찌개/탕류"), COFFEE("커피류"), NON_COFFEE("논커피류"), ETC("기타")
                    - restaurantType: 검색 식당 종류(RestaurantType enum)  
                        - ALL("전체 식당"), E_SQUARE("이스퀘어"),
                        - SALLY_BOX("샐리박스"), KYONGSUL("경슐랭")
                    - priceMin, priceMax: 가격 범위 필터링
                    
                    정렬 옵션 (sortingType)
                    - BASIC: 정렬 없음 (기본값)
                    - REVIEW_COUNT_DESC: 리뷰 많은 순
                    - RATING_DESC: 평점 높은 순
                    - PRICE_ASC: 가격 낮은 순
                    - PRICE_DESC: 가격 높은 순
                    
                    * 평점(averageRating)·리뷰 수(reviewCount)도 함께 반환됩니다 *
                    """
    )
    public ResponseEntity<?> search(
            @Parameter(description = "검색 요청 DTO (키워드, 타입, 가격 범위 등)")
            SearchRequestDTO DTO
    ) {
        Object result = searchService.search(DTO);
        Map<String, Object> response = Map.of("result", result);
        return ResponseEntity.ok(response);
    }
}
