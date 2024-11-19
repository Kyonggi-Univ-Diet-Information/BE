package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.FavoriteRestaurantReviewControllerDocs;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteRestaurantReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteRestaurantReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/restaurant")
@Slf4j
@Tag(name = "식당 리뷰 추천 API", description = "식당에 대한 리뷰 추천 API 입니다. (조회, 생성)")
public class FavoriteRestaurantReviewController implements FavoriteRestaurantReviewControllerDocs {

    private final FavoriteRestaurantReviewService favoriteRestaurantReviewService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 관심 식당 리뷰 1개 조회
     * @param id (Long)
     * @return FavoriteRestaurantReviewDTO
     */
    @GetMapping("/{id}")
    public FavoriteRestaurantReviewDTO findOne(@PathVariable("id") Long id) {
        return favoriteRestaurantReviewService.findById(id);
    }

    /**
     * 관심 식당 리뷰 전체 조회
     * @return List<FavoriteRestaurantReviewDTO>
     */
    @GetMapping("/all")
    public List<FavoriteRestaurantReviewDTO> findAll() {
        return favoriteRestaurantReviewService.findAll();
    }

    /**
     * 멤버별 관심 식당 리뷰 전체 조회
     * @param token (String)
     * @return List<FavoriteRestaurantReviewDTO>
     */
    @GetMapping("/each-member/all")
    public List<FavoriteRestaurantReviewDTO> findAllByMember(@RequestHeader("Authorization") String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        return favoriteRestaurantReviewService.findFavoriteRestaurantReviewListByMember(email);
    }

    /**
     * 멤버별 관심 식당 리뷰 생성
     * @param token (String)
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @PostMapping("/{reviewId}/create-favorite")
    public ResponseEntity<String> createFavoriteRestaurantReview(@RequestHeader("Authorization") String token,
                                                                 @PathVariable("reviewId") Long reviewId) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        favoriteRestaurantReviewService.createFavoriteRestaurantReview(reviewId, email);

        return ResponseEntity.ok("Successfully favorite");
    }

    /**
     * 관심 식당 리뷰 삭제
     * @param reviewId (Long)
     * @param token (String)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                               @RequestHeader("Authorization") String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        favoriteRestaurantReviewService.deleteFavoriteReview(email, reviewId);
        return ResponseEntity.ok("Review Deleted");
    }
}