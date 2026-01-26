package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.moderation.report.ReportReasonType;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonEtcDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "리뷰 API (통합)", description = "식당 종류(RestaurantType)에 따라 자동 분기되는 리뷰 API")
public interface ReviewControllerDocs {

    // ---------------------- 리뷰 생성 ----------------------
    @Operation(
            summary = "리뷰 생성",
            description = "지정한 음식(foodId)에 대해 리뷰를 작성합니다."
    )
    @ApiResponse(responseCode = "200", description = "리뷰 작성 완료")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/new/{foodId}")
    ResponseEntity<?> createReview(
            @Parameter(name = "type", description = "식당 종류 (DORMITORY, KYONGSUL 등)", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,

            @Parameter(name = "foodId", description = "리뷰를 등록할 음식 ID", in = ParameterIn.PATH)
            @PathVariable Long foodId,

            @Parameter(name = "Authorization", description = "Bearer JWT 토큰", in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String token,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 작성 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewDTO.class))
            )
            @RequestBody CreateReviewDTO dto
    );

    // ---------------------- 리뷰 조회 ----------------------
    @Operation(summary = "리뷰 단일 조회", description = "리뷰 ID를 기준으로 상세 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    @GetMapping("/one/{reviewId}")
    ResponseEntity<?> getReview(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "reviewId", description = "리뷰 ID", in = ParameterIn.PATH)
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails user
    );

    // ---------------------- 리뷰 수정 ----------------------
    @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    @ApiResponse(responseCode = "403", description = "작성자 본인이 아님")
    @PutMapping("/modify/{reviewId}")
    ResponseEntity<?> modify(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "reviewId", description = "리뷰 ID", in = ParameterIn.PATH)
            @PathVariable Long reviewId,
            @Parameter(name = "Authorization", description = "Bearer JWT 토큰", in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 리뷰 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewDTO.class))
            )
            @RequestBody CreateReviewDTO dto
    );

    // ---------------------- 리뷰 삭제 ----------------------
    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공")
    @ApiResponse(responseCode = "403", description = "작성자 본인이 아님")
    @DeleteMapping("/delete/{reviewId}")
    ResponseEntity<?> delete(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "reviewId", description = "리뷰 ID", in = ParameterIn.PATH)
            @PathVariable Long reviewId,
            @Parameter(name = "Authorization", description = "Bearer JWT 토큰", in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String token
    );

    // ---------------------- 평균 평점 ----------------------
    @Operation(summary = "평균 평점 조회", description = "해당 음식에 대한 리뷰의 평균 평점을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "평균 평점 조회 성공")
    @GetMapping("/average/{foodId}")
    ResponseEntity<?> average(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "foodId", description = "음식 ID", in = ParameterIn.PATH)
            @PathVariable Long foodId
    );

    // ---------------------- 점수별 리뷰 개수 ----------------------
    @Operation(summary = "점수별 리뷰 개수", description = "1~5점별 리뷰 개수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "점수별 개수 조회 성공")
    @GetMapping("/rating-count/{foodId}")
    ResponseEntity<?> ratingCount(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "foodId", description = "음식 ID", in = ParameterIn.PATH)
            @PathVariable Long foodId
    );

    // ---------------------- 리뷰 총 개수 ----------------------
    @Operation(summary = "음식별 리뷰 개수", description = "해당 음식에 달린 리뷰 총 개수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "총 개수 조회 성공")
    @GetMapping("/count/{foodId}")
    ResponseEntity<?> count(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "foodId", description = "음식 ID", in = ParameterIn.PATH)
            @PathVariable Long foodId
    );

    // ---------------------- 최신순 TOP5 ----------------------
    @Operation(summary = "최신 리뷰 TOP5", description = "가장 최근에 작성된 리뷰 5개를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/reviews/top5-recent")
    ResponseEntity<?> recent(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type
    );

    // ---------------------- 평점순 TOP5 ----------------------
    @Operation(summary = "평점순 리뷰 TOP5", description = "평점이 높은 순으로 상위 5개 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/reviews/top5-rating")
    ResponseEntity<?> top5(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type
    );

    // ---------------------- 페이징된 리뷰 조회 ----------------------
    @Operation(
            summary = "페이징된 리뷰 조회",
            description = "foodId에 해당하는 리뷰를 최신순으로 페이지 단위로 조회합니다. pageNo는 0부터 시작합니다."
    )
    @GetMapping("/paged/{foodId}")
    ResponseEntity<?> getPagedReviews(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "foodId", description = "음식 ID", in = ParameterIn.PATH)
            @PathVariable Long foodId,
            @Parameter(name = "pageNo", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY)
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @AuthenticationPrincipal CustomUserDetails user
            );

    @Operation(
            summary = "차단 기능",
            description = "reviewId에 해당하는 멤버를 차단합니다."
    )
    ResponseEntity<?> blockByReview(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,
            @Parameter(name = "reviewId", description = "리뷰 ID", in = ParameterIn.PATH)
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails me
    );


    @Operation(
            summary = "최신 리뷰 Top5 조회(version2)",
            description = "경슐랭, 이스퀘어, 샐리박스 전체 식당을 통합하여 가장 최신 리뷰 5개를 조회합니다"
    )
    ResponseEntity<?> getTop5RecentReviews();

    // ---------------------- 신고 사유 목록 조회 ----------------------
    @Operation(
            summary = "신고 사유 목록 조회",
            description = "리뷰 신고 시 선택할 수 있는 모든 신고 사유(enum)와 설명을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "신고 사유 목록 조회 성공")
    @GetMapping("/report/reasons")
    ResponseEntity<?> getReportReasons();

    // ---------------------- 리뷰 신고 ----------------------
    @Operation(
            summary = "리뷰 신고",
            description = """
                    특정 리뷰를 신고합니다.
                    
                    - 신고 사유는 enum 값으로 전달합니다.
                    - 신고 사유가 ETC인 경우, 기타 사유 내용은 RequestBody로 전달해야 합니다.
                    - 동일한 리뷰에 대해 중복 신고는 불가능합니다.
                    """
    )
    @ApiResponse(responseCode = "200", description = "리뷰 신고 성공")
    @ApiResponse(responseCode = "400", description = "중복 신고 또는 요청 값 검증 실패")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @PostMapping("/{type}/report/{reviewId}/{reasonType}")
    ResponseEntity<?> reportByReview(
            @Parameter(name = "type", description = "식당 종류", in = ParameterIn.PATH)
            @PathVariable RestaurantType type,

            @Parameter(name = "reviewId", description = "신고할 리뷰 ID", in = ParameterIn.PATH)
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails me,
            @Parameter(
                    name = "reasonType",
                    description = "신고 사유 타입 (enum)",
                    in = ParameterIn.PATH,
                    schema = @Schema(implementation = ReportReasonType.class)
            )
            @PathVariable ReportReasonType reasonType,

            @RequestBody(required = false) ReportReasonEtcDto reason
    );
}
