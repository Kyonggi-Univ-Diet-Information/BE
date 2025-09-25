package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.FavoriteKyongsulFoodReviewControllerDocs;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteKyongsulFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/kyongsul-food")
@Slf4j
@Tag(name = "경슐랭 음식 리뷰 추천 API", description = "경슐랭 음식에 대한 리뷰 추천 API 입니다. (조회, 생성)")
public class FavoriteKyongsulFoodReviewController implements FavoriteKyongsulFoodReviewControllerDocs {

    private final FavoriteKyongsulFoodReviewService favoriteKyongsulFoodReviewService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 관심 경슐랭 음식 리뷰 1개 조회
     * @param id (Long)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(favoriteKyongsulFoodReviewService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching review: " + e.getMessage());
        }
    }

    /**
     * 관심 음식 리뷰 전체 조회
     * @return List<FavoriteKyongsulFoodReviewDTO>
     */
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(favoriteKyongsulFoodReviewService.findAll());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 전체 조회
     * @param token (String)
     * @return List<FavoriteKyongsulFoodReviewDTO>
     */
    @GetMapping("/each-member/all")
    public ResponseEntity<?> findAllByMember(@RequestHeader("Authorization") String token) {
        log.info("Received Authorization header: {}", token);
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            log.info("Extracted email: {}", email);
            return ResponseEntity.ok(favoriteKyongsulFoodReviewService.findFavoriteKyongsulFoodReviewListByMember(email));
        } catch (Exception e) {
            log.error("Error processing token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching member reviews: " + e.getMessage());
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 생성
     * @param token (String)
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @PostMapping("/{reviewId}/create-favorite")
    public ResponseEntity<?> createFavorite(@RequestHeader("Authorization") String token,
                                                                 @PathVariable("reviewId") Long reviewId) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            favoriteKyongsulFoodReviewService.createFavoriteKyongsulReview(reviewId, email);
            return ResponseEntity.ok("Successfully add favorite");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating favorite: " + e.getMessage());
        }
    }

    /**
     * 관심 음식 리뷰 삭제
     * @param reviewId (Long)
     * @param token    (String)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") Long reviewId,
                                               @RequestHeader("Authorization") String token) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            favoriteKyongsulFoodReviewService.deleteFavoriteReview(email, reviewId);
            return ResponseEntity.ok("Favorite deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting favorite now: " + e.getMessage());
        }
    }

    /**
     * 해당 리뷰의 좋아요 수 조회
     * @param reviewId (Long)
     * @return ResponseEntity<Long>
     */
    @GetMapping("/count/{reviewId}")
    public ResponseEntity<?> getFavoriteCount(@PathVariable("reviewId") Long reviewId) {
        try {
            Long count = favoriteKyongsulFoodReviewService.getFavoriteReviewCountByReviewId(reviewId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred now: " + e.getMessage());
        }
    }
}
