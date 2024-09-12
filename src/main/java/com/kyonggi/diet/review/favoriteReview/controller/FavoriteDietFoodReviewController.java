package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/dietFood")
@Slf4j
public class FavoriteDietFoodReviewController {

    private final FavoriteDietFoodReviewService favoriteDietFoodReviewService;

    /**
     * 관심 음식 리뷰 1개 조회
     * @param id (Long)
     * @return FavoriteDietFoodReviewDTO
     */
    @GetMapping("/{id}")
    public FavoriteDietFoodReviewDTO findOne(@PathVariable("id") Long id) {
        return favoriteDietFoodReviewService.findById(id);
    }

    /**
     * 관심 음식 리뷰 전체 조회
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @GetMapping("/all")
    public List<FavoriteDietFoodReviewDTO> findAll() {
        return favoriteDietFoodReviewService.findAll();
    }

    /**
     * 멤버별 관심 음식 리뷰 전체 조회
     * @param memberId (Long)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @GetMapping("/{id}/all")
    public List<FavoriteDietFoodReviewDTO> findAllByMemberId(@PathVariable("id") Long memberId) {
        return favoriteDietFoodReviewService.findFavoriteDietFoodReviewListByMember(memberId);
    }

    /**
     * 멤버별 관심 음식 리뷰 생성
     * @param memberId (Long)
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @PostMapping("/{memberId}/{reviewId}/createFavorite")
    public ResponseEntity<String> createFavoriteDietFoodReview(@PathVariable("memberId") Long memberId,
                                                                 @PathVariable("reviewId") Long reviewId) {
        favoriteDietFoodReviewService.createFavoriteDietFoodReview(reviewId, memberId);
        return ResponseEntity.ok("Successfully favorite");
    }
}
