package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.FavoriteDietFoodReviewControllerDocs;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/diet-food")
@Slf4j
@Tag(name = "음식 리뷰 추천 API", description = "음식에 대한 리뷰 추천 API 입니다. (조회, 생성)")
public class FavoriteDietFoodReviewController implements FavoriteDietFoodReviewControllerDocs {

    private final FavoriteDietFoodReviewService favoriteDietFoodReviewService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 관심 음식 리뷰 1개 조회
     * @param id (Long)
     * @return FavoriteDietFoodReviewDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(favoriteDietFoodReviewService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching review: " + e.getMessage());
        }
    }

    /**
     * 관심 음식 리뷰 전체 조회
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(favoriteDietFoodReviewService.findAll());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 전체 조회
     * @param token (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @GetMapping("/each-member/all")
    public ResponseEntity<?> findAllByMember(@RequestHeader("Authorization") String token) {
        log.info("Received Authorization header: {}", token);
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            log.info("Extracted email: {}", email);
            return ResponseEntity.ok(favoriteDietFoodReviewService.findFavoriteDietFoodReviewListByMember(email));
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
    public ResponseEntity<?> createFavoriteDietFoodReview(@RequestHeader("Authorization") String token,
                                                                 @PathVariable("reviewId") Long reviewId) {
        try {
            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
            favoriteDietFoodReviewService.createFavoriteDietFoodReview(reviewId, email);
            return ResponseEntity.ok("Successfully added to favorites");
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
            favoriteDietFoodReviewService.deleteFavoriteReview(email, reviewId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting review now: " + e.getMessage());
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
            Long count = favoriteDietFoodReviewService.getFavoriteReviewCountByReviewId(reviewId);
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

    /**
     * 기숙사 식당 베스트(인기) TOP 리뷰 조회
     */
    @GetMapping("/reviews/best5")
    public ResponseEntity<List<ForTopReviewDTO>> getBestDietFoodReviews() {
        List<ForTopReviewDTO> reviews = favoriteDietFoodReviewService.find5DietFoodReviewsBest();
        return ResponseEntity.ok(reviews);
    }
}
