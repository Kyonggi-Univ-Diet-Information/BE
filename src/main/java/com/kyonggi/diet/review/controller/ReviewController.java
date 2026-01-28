package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.ReviewControllerDocs;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.review.DTO.*;
import com.kyonggi.diet.review.domain.Review;
import com.kyonggi.diet.review.moderation.block.BlockService;
import com.kyonggi.diet.review.moderation.report.ReportReasonType;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonDto;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonEtcDto;
import com.kyonggi.diet.review.moderation.report.ReportService;
import com.kyonggi.diet.review.service.EtcReviewService;
import com.kyonggi.diet.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Slf4j
@CrossOrigin("*")
public class ReviewController implements ReviewControllerDocs {

    private final JwtTokenUtil jwtTokenUtil;
    private final BlockService blockService;
    private final ReportService reportService;
    private final List<ReviewService<?>> reviewServices;
    private final Map<RestaurantType, ReviewService<?>> serviceMap = new EnumMap<>(RestaurantType.class);
    private final EtcReviewService eTCReviewService;

    @PostConstruct
    public void initServiceMap() {
        for (ReviewService<?> service : reviewServices) {
            serviceMap.put(service.getRestaurantType(), service);
            log.info("✅ Registered ReviewService for {}", service.getRestaurantType());
        }
    }

    private ReviewService<?> resolve(RestaurantType type) {
        return Optional.ofNullable(serviceMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported RestaurantType: " + type));
    }

    /** 리뷰 생성 */
    @PostMapping("/{type}/new/{foodId}")
    public ResponseEntity<?> createReview(@PathVariable("type") RestaurantType type,
                                          @PathVariable("foodId") Long foodId,
                                          @RequestHeader("Authorization") String token,
                                          @RequestBody CreateReviewDTO dto) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            ReviewDTO review = ReviewDTO.builder()
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .rating(dto.getRating())
                    .build();
            resolve(type).createReview(review, foodId, email);
            return ResponseEntity.ok("Review Created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 한개 조회 */
    @GetMapping("/{type}/one/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable("type") RestaurantType type,
                                       @PathVariable("reviewId") Long reviewId,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        try {
            return ResponseEntity.ok(resolve(type).findReviewDTO(reviewId, user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 페이징 조회 */
    @GetMapping("/{type}/paged/{foodId}")
    public ResponseEntity<?> getPagedReviews(@PathVariable("type") RestaurantType type,
                                             @PathVariable("foodId") Long foodId,
                                             @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                                             @AuthenticationPrincipal CustomUserDetails user) {
        try {
            Page<ReviewDTO> page = resolve(type).getAllReviewsByFoodIdPaged(foodId, pageNo, user);
            return ResponseEntity.ok(page);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 수정 */
    @PutMapping("/{type}/modify/{reviewId}")
    public ResponseEntity<?> modify(@PathVariable("type") RestaurantType type,
                                    @PathVariable("reviewId") Long reviewId,
                                    @RequestHeader("Authorization") String token,
                                    @RequestBody CreateReviewDTO dto) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            ReviewService service = resolve(type);
            if (!service.verifyMember(reviewId, email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized.");
            }
            service.modifyReview(reviewId, dto);
            return ResponseEntity.ok("Review Modified");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{type}/delete/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable("type") RestaurantType type,
                                    @PathVariable("reviewId") Long reviewId,
                                    @RequestHeader("Authorization") String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            ReviewService service = resolve(type);
            if (!service.verifyMember(reviewId, email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized.");
            }
            service.deleteReview(reviewId);
            return ResponseEntity.ok("Review Deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 평점 평균 조회 */
    @GetMapping("/{type}/average/{foodId}")
    public ResponseEntity<?> average(@PathVariable("type") RestaurantType type,
                                     @PathVariable("foodId") Long foodId) {
        try {
            return ResponseEntity.ok(resolve(type).getAverageRating(foodId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 점수별 개수 카운트 */
    @GetMapping("/{type}/rating-count/{foodId}")
    public ResponseEntity<?> ratingCount(@PathVariable("type") RestaurantType type,
                                         @PathVariable("foodId") Long foodId) {
        try {
            Map<Integer, Long> ratingCounts = resolve(type).getCountEachRating(foodId);
            return ResponseEntity.ok(new RatingCountResponse(ratingCounts));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 개수 조회 */
    @GetMapping("/{type}/count/{foodId}")
    public ResponseEntity<?> count(@PathVariable("type") RestaurantType type,
                                   @PathVariable("foodId") Long foodId) {
        try {
            return ResponseEntity.ok(resolve(type).getReviewCount(foodId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /**
     * 최신 TOP5 리뷰 Version 2. 식당 전체
     */
    @GetMapping("/top5-recent")
    public ResponseEntity<?> getTop5RecentReviews(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            List<RecentReviewDTO> result = eTCReviewService.getRecentTop5(user);

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "리뷰 데이터가 존재하지 않습니다."));
            }

            return ResponseEntity.ok(Map.of("result", result));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ENUM 매핑 실패", "message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "요청 처리 중 오류 발생", "message", e.getMessage()));
        }
    }

    /** 최신 TOP5 리뷰 */
    @GetMapping("/{type}/reviews/top5-recent")
    public ResponseEntity<?> recent(@PathVariable("type") RestaurantType type) {
        try {
            return ResponseEntity.ok(resolve(type).getRecentTop5());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 평점 TOP5 리뷰 */
    @GetMapping("/{type}/reviews/top5-rating")
    public ResponseEntity<?> top5(@PathVariable("type") RestaurantType type) {
        try {
            return ResponseEntity.ok(resolve(type).getTop5ByRating());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    @PostMapping("/{type}/block/{reviewId}")
    public ResponseEntity<?> blockByReview(
            @PathVariable RestaurantType type,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails me
            ) {
        Review review = resolve(type).getReview(reviewId);
        blockService.block(me.getMemberId(), review.getMember().getId());
        return ResponseEntity.ok("Review Blocked");
    }

    @Operation(
            summary = "신고 사유 목록 조회",
            description = "리뷰 신고 시 선택할 수 있는 모든 신고 사유(enum)와 설명을 조회합니다."
    )
    @GetMapping("/report/reasons")
    public ResponseEntity<?> getReportReasons() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", reportService.getAllReportReasonTypes());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{type}/report/{reviewId}/{reasonType}")
    public ResponseEntity<?> reportByReview(
            @PathVariable RestaurantType type,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable ReportReasonType reasonType,
            @RequestBody(required = false) ReportReasonEtcDto reason
    ) {
        if (reportService.existReportForReview(me, reviewId, type)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 신고한 게시물입니다.");
        }

        Review review = resolve(type).getReview(reviewId);
        try {
            if (reasonType != ReportReasonType.ETC
                    && reason != null
                    && reason.getEtcReason() != null) {
                return ResponseEntity.badRequest()
                        .body("ETC가 아닌 탈퇴 사유에는 기타 사유를 입력할 수 없습니다.");
            }

            ReportReasonDto dto = ReportReasonDto.builder()
                    .type(reasonType).build();

            if (reason != null) {
                dto.setEtcReason(reason.getEtcReason());
            }

            reportService.report(me, type, review, reviewId, dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.ok("Review reported");
    }
}
