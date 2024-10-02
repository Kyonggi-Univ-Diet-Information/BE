package com.kyonggi.diet.review.controller;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.RestaurantReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/restaurant")
@Slf4j
@CrossOrigin("*")
public class RestaurantReviewController {

    private final RestaurantReviewService restaurantReviewService;

    /**
     * 식당 리뷰 1개 조회
     * @param reviewId (Long)
     * @return ReviewDTO
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO oneReview(@PathVariable("id") Long reviewId) {
        return restaurantReviewService.findReview(reviewId);
    }

    /**
     * 식당 리뷰 모두 조회
     * @return List<ReviewDTO>
     */
    @GetMapping("/all")
    @ResponseBody
    public List<ReviewDTO> allReview() {
        return restaurantReviewService.findAllReview();
    }

    /**
     * 식당 리뷰 생성
     * @param memberId (Long)
     * @param type (RestaurantType)
     * @param createReviewDTO (CreateReviewDTO)
     * @return ResponseEntity
     */
    @PostMapping("/new/{restaurant}/{id}")
    public ResponseEntity<String> createRestaurantReview(@PathVariable("id") Long memberId
                                               , @PathVariable("restaurant") RestaurantType type
                                               , @RequestBody CreateReviewDTO createReviewDTO) {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(createReviewDTO.getRating())
                .title(createReviewDTO.getTitle())
                .content(createReviewDTO.getContent())
                .build();
        restaurantReviewService.createRestaurantReview(reviewDTO, type, memberId);

        return ResponseEntity.ok("Review Created");
    }

    /**
     * 식당 리뷰 수정
     * @param reviewId (Long)
     * @param reviewDTO (ReviewDTO)
     * @return ResponseEntity
     */
    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        restaurantReviewService.modifyReview(reviewId, reviewDTO);
        return ResponseEntity.ok("Review Updated");
    }

    /**
     * 식당 리뷰 삭제
     * @param reviewId (Long)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId) {
        restaurantReviewService.deleteReview(reviewId);
        log.info("삭제된 id : " + reviewId);
        return ResponseEntity.ok("Review Deleted");
    }

    /**
     * 타입별 식당 리뷰 조회
     * @return List<ReviewDTO>
     */
    @GetMapping("/type/{type}")
        public List<ReviewDTO> findReviewsByType(@PathVariable("type") RestaurantType type) {
            return restaurantReviewService.findReviewsByType(type);
    }

}
