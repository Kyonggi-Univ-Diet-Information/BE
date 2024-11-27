package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface DietFoodReviewControllerDocs {

    @Operation(summary = "특정 음식 리뷰 1개 조회", description = "음식 리뷰 ID 값으로 특정 음식 리뷰 1개를 조회하는 API")
    @Parameter(name = "id", description = "리뷰 ID")
    public ReviewDTO oneReview(@PathVariable("id") Long reviewId);

    @Operation(summary = "모든 음식 리뷰 조회", description = "DB에 저장된 모든 음식 리뷰를 조회하는 API")
    public List<ReviewDTO> allReview();

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
    public List<ReviewDTO> allReviewsById(@PathVariable("dietFoodId") Long dietFoodId);

    @Operation(summary = "특정 음식 리뷰 평점 구하기", description = "사용자로부터 음식 리뷰 id를 입력받아, 특정 음식의 리뷰 평점을 반환하는 API")
    @Parameter(name = "dietFoodId", description = "음식 ID")
    public Double getAverageRating(@PathVariable("dietFoodId") Long dietFoodId);
}
