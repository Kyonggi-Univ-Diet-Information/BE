package com.kyonggi.diet.review.favoriteReview.controller;

import com.kyonggi.diet.controllerDocs.FavoriteDietFoodReviewControllerDocs;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/review/favorite/diet-food")
@Slf4j
@Tag(name = "음식 리뷰 추천 API", description = "음식에 대한 리뷰 추천 API 입니다. (조회, 생성)")
public class FavoriteDietFoodReviewController implements FavoriteDietFoodReviewControllerDocs {

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
     * @param email (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @GetMapping("/{email}/all")
    public List<FavoriteDietFoodReviewDTO> findAllByMemberId(@PathVariable("email") String email) {
        return favoriteDietFoodReviewService.findFavoriteDietFoodReviewListByMember(email);
    }

    /**
     * 멤버별 관심 음식 리뷰 생성
     * @param email (String)
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @PostMapping("/{email}/{reviewId}/create-favorite")
    public ResponseEntity<String> createFavoriteDietFoodReview(@PathVariable("email") String email,
                                                                 @PathVariable("reviewId") Long reviewId) {
        favoriteDietFoodReviewService.createFavoriteDietFoodReview(reviewId, email);
        return ResponseEntity.ok("Successfully favorite");
    }
}
