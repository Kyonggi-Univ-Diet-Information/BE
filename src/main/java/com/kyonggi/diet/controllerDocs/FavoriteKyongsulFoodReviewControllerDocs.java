package com.kyonggi.diet.controllerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface FavoriteKyongsulFoodReviewControllerDocs {

    @Operation(
        summary = "관심 경슐랭 리뷰 단건 조회",
        description = "리뷰 ID를 통해 관심 등록된 경슐랭 리뷰 1건을 조회합니다."
    )
    ResponseEntity<?> findOne(
        @Parameter(description = "리뷰 ID", example = "1")
        @PathVariable("id") Long id
    );

    @Operation(
        summary = "전체 관심 경슐랭 리뷰 조회",
        description = "모든 유저가 등록한 관심 리뷰 데이터를 전체 조회합니다."
    )
    ResponseEntity<?> findAll();

    @Operation(
        summary = "멤버별 관심 경슐랭 리뷰 전체 조회",
        description = "토큰을 통해 현재 로그인한 유저의 관심 경슐랭 리뷰 리스트를 조회합니다."
    )
    ResponseEntity<?> findAllByMember(
        @Parameter(description = "JWT 토큰 (Bearer 포함)", required = true)
        @RequestHeader("Authorization") String token
    );

    @Operation(
        summary = "관심 리뷰 등록",
        description = "특정 리뷰를 관심(좋아요) 리뷰로 등록합니다."
    )
    ResponseEntity<?> createFavorite(
        @Parameter(description = "JWT 토큰 (Bearer 포함)", required = true)
        @RequestHeader("Authorization") String token,

        @Parameter(description = "등록할 리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId
    );

    @Operation(
        summary = "관심 리뷰 삭제",
        description = "특정 리뷰의 관심(좋아요)을 취소(삭제)합니다."
    )
    ResponseEntity<?> deleteReview(
        @Parameter(description = "삭제할 리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId,

        @Parameter(description = "JWT 토큰 (Bearer 포함)", required = true)
        @RequestHeader("Authorization") String token
    );

    @Operation(
        summary = "관심 수 조회",
        description = "특정 리뷰가 받은 총 관심(좋아요) 수를 조회합니다."
    )
    ResponseEntity<?> getFavoriteCount(
        @Parameter(description = "리뷰 ID", example = "1")
        @PathVariable("reviewId") Long reviewId
    );
}
