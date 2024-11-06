package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface RestaurantReviewControllerDocs {

    @Operation(summary = "특정 식당 리뷰 1개 조회", description = "식당 리뷰 ID 값으로 특정 음식 리뷰 1개를 조회하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    public ReviewDTO oneReview(@PathVariable("id") Long reviewId);

    @Operation(summary = "모든 식당 리뷰 조회", description = "DB에 저장된 모든 식당 리뷰를 조회하는 API")
    public List<ReviewDTO> allReview();

    @Operation(summary = "특정 식당에 리뷰 남기기", description = "사용자로부터 요청 값을 받아, 특정 식당에 리뷰를 남기는 API")
    @Parameter(name = "email", description = "사용자 이메일")
    @Parameter(name = "restaurant", description = "식당 종류")
    @Parameter(name = "rating", description = "리뷰 별점")
    @Parameter(name = "title", description = "리뷰 제목")
    @Parameter(name = "content", description = "리뷰 내용")
    public ResponseEntity<String> createRestaurantReview(@PathVariable("email") String email
                                                   , @PathVariable("restaurant") RestaurantType type
                                                   , @RequestBody CreateReviewDTO createReviewDTO);

    @Operation(summary = "특정 식당 리뷰 수정", description = "사용자보루터 요청 값을 받아, 특정 식당 리뷰를 수정하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    @Parameter(name = "email", description = "사용자 이메일")
    @Parameter(name = "rating", description = "별점")
    @Parameter(name = "title", description = "리뷰 제목")
    @Parameter(name = "content", description = "리뷰 내용")
    @Parameter(name = "memberName", description = "사용X")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email,
                                               @RequestBody ReviewDTO reviewDTO);

    @Operation(summary = "특정 식당 리뷰 삭제", description = "사용자로부터 요청받아, 특정 식당 리뷰를 삭제하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    @Parameter(name = "email", description = "사용자 이메일")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email);

    @Operation(summary = "특정 종류의 식당 리뷰 모두 조회", description = "사용자로부터 요청받아, 특정 종류의 식당 리뷰를 모두 조희")
    @Parameter(name = "type", description = "식당 종류")
    public List<ReviewDTO> findReviewsByType(@PathVariable("type") RestaurantType type);
}
