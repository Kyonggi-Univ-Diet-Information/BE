package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface FavoriteDietFoodReviewControllerDocs {

    @Operation(summary = "특정 추천 음식 리뷰 1개 조회", description = "추천 음식 리뷰 ID로 추천 리뷰 1개 조회하는 API")
    @Parameter(name = "id", description = "추천 리뷰 ID")
    public FavoriteDietFoodReviewDTO findOne(@PathVariable("id") Long id);

    @Operation(summary = "추천 음식 리뷰 모두 조회", description = "DB에 저장된 추천 음식 리뷰 모두 조회하는 API")
    public List<FavoriteDietFoodReviewDTO> findAll();

    @Operation(summary = "특정 회원이 추천한 음식 리뷰 모두 조회", description = "특정 회원이 추천한 음식 리뷰 모두 조회")
    public List<FavoriteDietFoodReviewDTO> findAllByMember(@RequestHeader("Authorization") String token);

    @Operation(summary = "특정 음식 리뷰 추천하기", description = "사용자가 특정 음식 리뷰에 좋아요 표시하는 API")
    @Parameter(name = "reviewId", description = "리뷰 ID")
    public ResponseEntity<String> createFavoriteDietFoodReview(@RequestHeader("Authorization") String token,
                                                               @PathVariable("reviewId") Long reviewId);
}
