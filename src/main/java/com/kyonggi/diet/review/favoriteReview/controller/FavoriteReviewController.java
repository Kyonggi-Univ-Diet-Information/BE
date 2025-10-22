package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.FavoriteReviewControllerDocs;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 통합 리뷰 좋아요 컨트롤러
 * - RestaurantType별 FavoriteReviewService 자동 매핑
 * - if / instanceof 없음
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/{type}")
@Slf4j
@Tag(name = "리뷰 좋아요 통합 API", description = "RestaurantType에 따라 자동 분기되는 리뷰 좋아요 API")
public class FavoriteReviewController implements FavoriteReviewControllerDocs {

    private final JwtTokenUtil jwtTokenUtil;
    private final List<FavoriteReviewService> favoriteReviewServices;
    private final Map<RestaurantType, FavoriteReviewService> serviceMap = new EnumMap<>(RestaurantType.class);

    @PostConstruct
    public void initServiceMap() {
        for (FavoriteReviewService service : favoriteReviewServices) {
            serviceMap.put(service.getRestaurantType(), service);
            log.info("✅ Registered FavoriteReviewService for {}", service.getRestaurantType());
        }
    }

    private FavoriteReviewService resolve(RestaurantType type) {
        return Optional.ofNullable(serviceMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported RestaurantType: " + type));
    }

    /** 리뷰 좋아요 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable("type") RestaurantType type,
                                     @PathVariable("id") Long id) {
        try {
            Object dto = resolve(type).findById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid restaurant type: " + type);
        } catch (Exception e) {
            log.error("Error fetching favorite review", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("요청은 유효하지만 처리 중 문제가 발생했습니다.");
        }
    }

    /** 멤버별 좋아요 리뷰 목록 조회 */
    @GetMapping("/each-member/all")
    public ResponseEntity<?> findAllByMember(@PathVariable("type") RestaurantType type,
                                             @RequestHeader("Authorization") String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            return ResponseEntity.ok(resolve(type).findFavoriteReviewListByMember(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid restaurant type: " + type);
        } catch (Exception e) {
            log.error("Error fetching member favorite reviews", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("요청은 유효하지만 처리 중 문제가 발생했습니다.");
        }
    }

    /** 좋아요 생성 */
    @PostMapping("/{reviewId}/create-favorite")
    public ResponseEntity<?> createFavorite(@PathVariable("type") RestaurantType type,
                                            @PathVariable("reviewId") Long reviewId,
                                            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            resolve(type).createFavoriteReview(reviewId, email);
            return ResponseEntity.ok("Successfully added to favorites");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid restaurant type: " + type);
        } catch (Exception e) {
            log.error("Error creating favorite", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("요청은 유효하지만 처리 중 문제가 발생했습니다." + e.getMessage());
        }
    }

    /** 좋아요 삭제 */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable("type") RestaurantType type,
                                            @PathVariable("reviewId") Long reviewId,
                                            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            resolve(type).deleteFavoriteReview(email, reviewId);
            return ResponseEntity.ok("Favorite deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid restaurant type: " + type);
        } catch (Exception e) {
            log.error("Error deleting favorite", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("요청은 유효하지만 처리 중 문제가 발생했습니다.");
        }
    }

    /** 좋아요 개수 조회 */
    @GetMapping("/count/{reviewId}")
    public ResponseEntity<?> getFavoriteCount(@PathVariable("type") RestaurantType type,
                                              @PathVariable("reviewId") Long reviewId) {
        try {
            Long count = resolve(type).getFavoriteReviewCountByReviewId(reviewId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid restaurant type: " + type);
        } catch (Exception e) {
            log.error("Error fetching favorite count", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("요청은 유효하지만 처리 중 문제가 발생했습니다.");
        }
    }
}
