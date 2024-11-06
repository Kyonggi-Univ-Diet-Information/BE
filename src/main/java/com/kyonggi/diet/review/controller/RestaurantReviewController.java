package com.kyonggi.diet.review.controller;
import com.kyonggi.diet.controllerDocs.RestaurantReviewControllerDocs;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.RestaurantReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "식당 리뷰 API", description = "식당에 대한 리뷰 API 입니다. (조회, 생성, 삭제, 수정)")
public class RestaurantReviewController implements RestaurantReviewControllerDocs {

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
     * @param email (String)
     * @param type (RestaurantType)
     * @param createReviewDTO (CreateReviewDTO)
     * @return ResponseEntity
     */
    @PostMapping("/new/{restaurant}/{email}")
    public ResponseEntity<String> createRestaurantReview(@PathVariable("email") String email
                                               , @PathVariable("restaurant") RestaurantType type
                                               , @RequestBody CreateReviewDTO createReviewDTO) {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(createReviewDTO.getRating())
                .title(createReviewDTO.getTitle())
                .content(createReviewDTO.getContent())
                .build();
        restaurantReviewService.createRestaurantReview(reviewDTO, type, email);

        return ResponseEntity.ok("Review Created");
    }

    /**
     * 식당 리뷰 수정
     * @param reviewId (Long)
     * @param email (String)
     * @param reviewDTO (ReviewDTO)
     * @return ResponseEntity
     */
    @PutMapping("/modify/{id}/{email}")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email,
                                               @RequestBody ReviewDTO reviewDTO) {

        if (!restaurantReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

        restaurantReviewService.modifyReview(reviewId, reviewDTO);
        return ResponseEntity.ok("Review Updated");
    }

    /**
     * 식당 리뷰 삭제
     * @param reviewId (Long)
     * @param email (String)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{id}/{email}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email) {
        if (!restaurantReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

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
