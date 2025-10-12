package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface KyongsulReviewControllerDocs {

    /**
     * 음식 리뷰 생성 API
     */
    @Operation(
        summary = "경슐랭 음식 리뷰 생성",
        description = "특정 음식(foodId)에 대한 리뷰를 생성합니다. Authorization 헤더에 유저 토큰이 필요합니다."
    )
    ResponseEntity<?> createReview(
        @Parameter(description = "리뷰를 작성할 음식 ID", example = "1")
        @PathVariable("foodId") Long foodId,

        @Parameter(description = "Access Token (Bearer 포함)", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String token,

        @Parameter(description = "생성할 리뷰 데이터", required = true)
        @RequestBody CreateReviewDTO createReviewDTO
    );

    /**
     * 리뷰 한 개 조회 API
     */
    @Operation(
        summary = "경슐랭 리뷰 단일 조회",
        description = "리뷰 ID를 기준으로 하나의 리뷰를 조회합니다."
    )
    ResponseEntity<?> getOne(
        @Parameter(description = "조회할 리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId
    );

    /**
     * 페이징된 음식 리뷰 리스트 조회 API
     */
    @Operation(
        summary = "경슐랭 음식 리뷰 페이징 조회",
        description = "특정 음식에 대한 리뷰들을 페이징 처리하여 조회합니다. 기본 페이지 번호는 0입니다."
    )
    Page<ReviewDTO> getPage(
        @Parameter(description = "리뷰를 조회할 음식 ID", example = "1")
        @PathVariable("foodId") Long foodId,

        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
        @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo
    );

    /**
     * 경슐랭 음식 리뷰 삭제 API
     */
    @Operation(
        summary = "경슐랭 리뷰 삭제",
        description = "특정 리뷰를 삭제합니다. 해당 리뷰의 작성자여야 삭제가 가능합니다."
    )
    ResponseEntity<?> deleteOne(
        @Parameter(description = "삭제할 리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId,

        @Parameter(description = "Access Token (Bearer 포함)", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String token
    );

    /**
     * 경슐랭 음식 리뷰 수정 API
     */
    @Operation(
        summary = "경슐랭 리뷰 수정",
        description = "리뷰 ID를 기준으로 해당 리뷰를 수정합니다. 본인만 수정할 수 있습니다."
    )
    ResponseEntity<?> modifyOne(
        @Parameter(description = "수정할 리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId,

        @Parameter(description = "Access Token (Bearer 포함)", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String token,

        @Parameter(description = "수정할 리뷰 내용", required = true)
        @RequestBody CreateReviewDTO createReviewDTO
    );

    /**
     * 경슐랭 음식 리뷰 평점 평균 조회 API
     */
    @Operation(
        summary = "경슐랭 음식 평점 평균 조회",
        description = "특정 음식에 대해 작성된 리뷰들의 평균 평점을 조회합니다."
    )
    Double getAverageRatingByKyongsulFoodId(
        @Parameter(description = "음식 ID", example = "1")
        @PathVariable("kyongsulFoodId") Long kyongsulFoodId
    );

    @Operation(
        summary = "경술 음식 평점별 리뷰 개수 조회",
        description = "kyongsulFoodId를 기준으로 1~5점 평점별 리뷰 개수를 반환합니다. 평점이 없는 경우 0으로 채워집니다."
    )
    @Parameter(name = "kyongsulFoodId", description = "경술 음식 ID")
    @GetMapping("/rating-count/{kyongsulFoodId}")
    ResponseEntity<RatingCountResponse> getRatingCount(@PathVariable("kyongsulFoodId") Long kyongsulFoodId);
}
