package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteRestaurantReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteRestaurantReviewService;
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
public class FavoriteRestaurantReviewController {

    private final FavoriteRestaurantReviewService favoriteRestaurantReviewService;

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
     * @param email (String)
     * @return List<FavoriteRestaurantReviewDTO>
     */
    @GetMapping("/{email}/all")
    public List<FavoriteRestaurantReviewDTO> findAllByMemberId(@PathVariable("email") String email) {
        return favoriteRestaurantReviewService.findFavoriteRestaurantReviewListByMember(email);
    }

    /**
     * 멤버별 관심 식당 리뷰 생성
     * @param email (String)
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @PostMapping("/{email}/{reviewId}/create-favorite")
    public ResponseEntity<String> createFavoriteRestaurantReview(@PathVariable("email") String email,
                                                                 @PathVariable("reviewId") Long reviewId) {
        favoriteRestaurantReviewService.createFavoriteRestaurantReview(reviewId, email);
        return ResponseEntity.ok("Successfully favorite");
    }
}