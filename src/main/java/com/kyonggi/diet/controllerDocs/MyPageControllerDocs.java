package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface MyPageControllerDocs {

    @Operation(
            summary = "내 기본 정보 조회",
            description = "Access Token을 이용하여 사용자의 마이페이지 기본 정보를 조회합니다."
    )
    ResponseEntity<?> getMyInfo(
            @Parameter(description = "Bearer 토큰", required = true)
            @RequestHeader("Authorization") String token
    );

    @Operation(
            summary = "내가 쓴 리뷰 조회 (페이징)",
            description = "Access Token을 이용해 사용자가 작성한 리뷰 목록을 페이지 단위로 조회합니다."
    )
    ResponseEntity<?> getMyReviews(
            @Parameter(description = "Bearer 토큰", required = true)
            @RequestHeader("Authorization") String token,

            @Parameter(description = "음식점 종류 (KYONGSUL, DORMITORY, E_SQUARE)", required = true)
            @RequestParam("type") RestaurantType type,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page
    );

    @Operation(
            summary = "좋아요한 리뷰 조회 (페이징)",
            description = "Access Token을 이용해 사용자가 좋아요한 리뷰 목록을 페이지 단위로 조회합니다."
    )
    ResponseEntity<?> getMyFavoriteReviews(
            @Parameter(description = "Bearer 토큰", required = true)
            @RequestHeader("Authorization") String token,

            @Parameter(description = "음식점 종류 (KYONGSUL, DORMITORY, E_SQUARE)", required = true)
            @RequestParam("type") RestaurantType type,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page
    );
}
