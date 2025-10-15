package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface DietFoodReviewControllerDocs {

    @Operation(summary = "특정 음식 리뷰 1개 조회", description = "음식 리뷰 ID 값으로 특정 음식 리뷰 1개를 조회하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    public ResponseEntity<?> oneReview(@PathVariable("id") Long reviewId);

    @Operation(summary = "모든 음식 리뷰 조회", description = "DB에 저장된 모든 음식 리뷰를 조회하는 API")
    public ResponseEntity<?> allReview();

    @Operation(summary = "특정 음식에 리뷰 남기기", description = "사용자로부터 요청 값을 받아, 특정 음식에 리뷰를 남기는 API")
    @Parameter(name = "dietFoodId", description = "음식 ID")
    @Parameter(name = "rating", description = "리뷰 별점")
    @Parameter(name = "title", description = "리뷰 제목")
    @Parameter(name = "content", description = "리뷰 내용")
    public ResponseEntity<String> createDietFoodReview(@PathVariable("dietFoodId") Long dietFoodId,
                                                       @RequestHeader("Authorization") String token,
                                                       @RequestBody CreateReviewDTO createReviewDTO);

    @Operation(summary = "특정 음식 리뷰 수정", description = "사용자보루터 요청 값을 받아, 특정 음식 리뷰를 수정하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    @Parameter(name = "rating", description = "별점")
    @Parameter(name = "title", description = "리뷰 제목")
    @Parameter(name = "content", description = "리뷰 내용")
    @Parameter(name = "memberName", description = "사용X")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId,
                                               @RequestBody ReviewDTO reviewDTO,
                                               @RequestHeader("Authorization") String token);

    @Operation(summary = "특정 음식 리뷰 삭제", description = "사용자로부터 요청받아, 특정 음식 리뷰를 삭제하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId,
                                               @RequestHeader("Authorization") String token);

    @Operation(summary = "특정 음식 아이디로 리뷰 리스트 구하기", description = "사용자로부터, 음식 ID를 요청받아 특정 음식 리뷰 리스트를 구하는 API")
    @Parameter(name = "dietFoodId", description = "음식 ID")
    public ResponseEntity<?> allReviewsById(@PathVariable("dietFoodId") Long dietFoodId);

    @Operation(summary = "특정 음식 리뷰 평점 구하기", description = "사용자로부터 음식 리뷰 id를 입력받아, 특정 음식의 리뷰 평점을 반환하는 API")
    @Parameter(name = "dietFoodId", description = "음식 ID")
    public ResponseEntity<?> getAverageRating(@PathVariable("dietFoodId") Long dietFoodId);

    @Operation(summary = "페이징된 음식 리뷰 구하기", description = "원하는 페이지 입력시, 해당 페이지에 대한 음식 리뷰들을 반환하는 API (최신순으로 적용하여 마지막 리뷰가 1페이지에 있게 적용), 현재 각 페이지당 10개의 리뷰 확인하도록 설정")
    @Parameter(name = "pageNo", description = "페이지 번호 (default: 0) index 0이 1페이지이므로 주의. ")
    public ResponseEntity<?> getPagedDietFoodReviews(@PathVariable("dietFoodId") Long dietFoodId,
                                                    @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo);

    @Operation(
            summary = "일반 음식 평점별 리뷰 개수 조회",
            description = "dietFoodId를 기준으로 1~5점 평점별 리뷰 개수를 반환합니다. 평점이 없는 경우 0으로 채워집니다."
    )
    @Parameter(name = "dietFoodId", description = "일반 음식 ID")
    @GetMapping("/rating-count/{dietFoodId}")
    ResponseEntity<RatingCountResponse> getRatingCount(@PathVariable("dietFoodId") Long dietFoodId);

    /**
     * 기숙사 음식 메뉴에 해당하는 리뷰 카운트 반환
     */
    @Operation(
            summary = "기숙사 음식별 리뷰 개수 조회",
            description = "기숙사 음식별 리뷰 개수 조회"
    )
    @Parameter(name = "foodId", description = "기숙사 음식 ID")
    @GetMapping("/count/{foodId}")
    ResponseEntity<?> getDietFoodReviewCount(@PathVariable("foodId") Long dietFoodId);

    @Operation(
        summary = "기숙사 식당 음식 최신 리뷰 TOP 5 조회",
        description = """
        등록일(createdAt) 기준으로 최신순 정렬된 리뷰 중 상위 5개를 조회합니다.
        각 리뷰에는 음식 ID, 리뷰 ID, 별점, 제목, 내용, 작성자 정보가 포함됩니다.
        리뷰 개수가 5개 미만일 경우 존재하는 리뷰만 반환됩니다.
        """
    )
    @GetMapping("/reviews/recent")
    ResponseEntity<List<ForTopReviewDTO>> getRecentDietFoodReviews();
}
