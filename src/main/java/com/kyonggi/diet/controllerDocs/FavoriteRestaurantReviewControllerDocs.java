package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteRestaurantReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface FavoriteRestaurantReviewControllerDocs {

    @Operation(summary = "특정 추천 식당 리뷰 1개 조회", description = "추천 식당 리뷰 ID로 추천 리뷰 1개 조회하는 API")
    @Parameter(name = "id", description = "추천 리뷰 ID")
    public FavoriteRestaurantReviewDTO findOne(@PathVariable("id") Long id);

    @Operation(summary = "추천 식당 리뷰 모두 조회", description = "DB에 저장된 추천 식당 리뷰 모두 조회하는 API")
    public List<FavoriteRestaurantReviewDTO> findAll();

    @Operation(summary = "특정 회원이 추천한 식당 리뷰 모두 조회", description = "특정 회원이 추천한 식당 리뷰 모두 조회")
    @Parameter(name = "email", description = "사용자 이메일")
    public List<FavoriteRestaurantReviewDTO> findAllByMemberId(@PathVariable("email") String email);

    @Operation(summary = "특정 식당 리뷰 추천하기", description = "사용자가 특정 식당 리뷰에 좋아요 표시하는 API")
    @Parameter(name = "email", description = "사용자 이메일")
    @Parameter(name = "reviewId", description = "리뷰 ID")
    public ResponseEntity<String> createFavoriteRestaurantReview(@PathVariable("email") String email,
                                                               @PathVariable("reviewId") Long reviewId);
}
