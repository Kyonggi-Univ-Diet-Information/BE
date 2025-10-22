package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.ReviewControllerDocs;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.ReviewService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/{type}")
@Slf4j
@CrossOrigin("*")
public class ReviewController implements ReviewControllerDocs {

    private final JwtTokenUtil jwtTokenUtil;
    private final List<ReviewService> reviewServices;
    private final Map<RestaurantType, ReviewService> serviceMap = new EnumMap<>(RestaurantType.class);

    @PostConstruct
    public void initServiceMap() {
        for (ReviewService service : reviewServices) {
            serviceMap.put(service.getRestaurantType(), service);
            log.info("✅ Registered ReviewService for {}", service.getRestaurantType());
        }
    }

    private ReviewService resolve(RestaurantType type) {
        return Optional.ofNullable(serviceMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported RestaurantType: " + type));
    }

    /** 리뷰 생성 */
    @PostMapping("/new/{foodId}")
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
    @GetMapping("/one/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable("type") RestaurantType type,
                                       @PathVariable("reviewId") Long reviewId) {
        try {
            return ResponseEntity.ok(resolve(type).findReviewDTO(reviewId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 페이징 조회 */
    @GetMapping("/paged/{foodId}")
    public ResponseEntity<?> getPagedReviews(@PathVariable("type") RestaurantType type,
                                             @PathVariable("foodId") Long foodId,
                                             @RequestParam(name = "pageNo", defaultValue = "0") int pageNo) {
        try {
            Page<ReviewDTO> page = resolve(type).getAllReviewsByFoodIdPaged(foodId, pageNo);
            return ResponseEntity.ok(page);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 해당 음식에 대한 모든 리뷰 조회 */
    @GetMapping("/all/{foodId}")
    public ResponseEntity<?> getAllReviews(@PathVariable("type") RestaurantType type,
                                           @PathVariable("foodId") Long foodId) {
        try {
            return ResponseEntity.ok(resolve(type).getAllReviews(foodId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 수정 */
    @PutMapping("/modify/{reviewId}")
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
    @DeleteMapping("/delete/{reviewId}")
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
    @GetMapping("/average/{foodId}")
    public ResponseEntity<?> average(@PathVariable("type") RestaurantType type,
                                     @PathVariable("foodId") Long foodId) {
        try {
            return ResponseEntity.ok(resolve(type).getAverageRating(foodId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 리뷰 점수별 개수 카운트 */
    @GetMapping("/rating-count/{foodId}")
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
    @GetMapping("/count/{foodId}")
    public ResponseEntity<?> count(@PathVariable("type") RestaurantType type,
                                   @PathVariable("foodId") Long foodId) {
        try {
            return ResponseEntity.ok(resolve(type).getReviewCount(foodId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 최신 TOP5 리뷰 */
    @GetMapping("/reviews/top5-recent")
    public ResponseEntity<?> recent(@PathVariable("type") RestaurantType type) {
        try {
            return ResponseEntity.ok(resolve(type).getRecentTop5());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }

    /** 평점 TOP5 리뷰 */
    @GetMapping("/reviews/top5-rating")
    public ResponseEntity<?> top5(@PathVariable("type") RestaurantType type) {
        try {
            return ResponseEntity.ok(resolve(type).getTop5ByRating());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid restaurant type: " + type);
        }
    }
}
