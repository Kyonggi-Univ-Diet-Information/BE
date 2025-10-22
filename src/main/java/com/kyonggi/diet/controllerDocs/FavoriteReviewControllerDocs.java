package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface FavoriteReviewControllerDocs {

    // ---------------------- 좋아요 단건 조회 ----------------------
    @Operation(summary = "리뷰 좋아요 단건 조회", description = "특정 리뷰의 좋아요 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "요청은 유효하지만 처리 중 문제가 발생함")
    })
    ResponseEntity<?> findOne(
            @Parameter(description = "식당 타입", required = true) @PathVariable("type") RestaurantType type,
            @Parameter(description = "좋아요 리뷰 ID", required = true) @PathVariable("id") Long id
    );

    // ---------------------- 사용자별 좋아요 목록 조회 ----------------------
    @Operation(summary = "사용자별 좋아요 리뷰 목록 조회", description = "현재 로그인한 사용자의 좋아요 리뷰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "422", description = "요청은 유효하지만 처리 중 문제가 발생함")
    })
    ResponseEntity<?> findAllByMember(
            @Parameter(description = "식당 타입", required = true) @PathVariable("type") RestaurantType type,
            @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true) @RequestHeader("Authorization") String token
    );

    // ---------------------- 좋아요 추가 ----------------------
    @Operation(summary = "리뷰 좋아요 추가", description = "특정 리뷰를 사용자의 좋아요 목록에 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "409", description = "이미 좋아요한 리뷰"),
            @ApiResponse(responseCode = "422", description = "요청은 유효하지만 처리 중 문제가 발생함")
    })
    ResponseEntity<?> createFavorite(
            @Parameter(description = "식당 타입") @PathVariable("type") RestaurantType type,
            @Parameter(description = "리뷰 ID") @PathVariable("reviewId") Long reviewId,
            @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true) @RequestHeader("Authorization") String token
    );

    // ---------------------- 좋아요 삭제 ----------------------
    @Operation(summary = "리뷰 좋아요 삭제", description = "사용자의 좋아요 목록에서 해당 리뷰를 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "좋아요 리뷰를 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "요청은 유효하지만 처리 중 문제가 발생함")
    })
    ResponseEntity<?> deleteFavorite(
            @Parameter(description = "식당 타입") @PathVariable("type") RestaurantType type,
            @Parameter(description = "리뷰 ID") @PathVariable("reviewId") Long reviewId,
            @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true) @RequestHeader("Authorization") String token
    );

    // ---------------------- 좋아요 개수 조회 ----------------------
    @Operation(summary = "리뷰 좋아요 개수 조회", description = "특정 리뷰의 총 좋아요 개수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "422", description = "요청은 유효하지만 처리 중 문제가 발생함")
    })
    ResponseEntity<?> getFavoriteCount(
            @Parameter(description = "식당 타입") @PathVariable("type") RestaurantType type,
            @Parameter(description = "리뷰 ID") @PathVariable("reviewId") Long reviewId
    );
}
