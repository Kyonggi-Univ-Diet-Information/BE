package com.kyonggi.diet.review;
import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Slf4j
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 한개 (리뷰 클릭시 경로에 reviewId)
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO oneReview(@PathVariable("id") Long reviewId) {
        return reviewService.findReview(reviewId);
    }

    //모든 리뷰
    @GetMapping("/allReview")
    @ResponseBody
    public List<ReviewDTO> allReview() {
        return reviewService.findAllReview();
    }

    @PostMapping("/createReview/restaurant/{restaurant}/{id}")
    public ResponseEntity<String> createRestaurantReview(@PathVariable("id") Long memberId
                                               , @PathVariable("restaurant") RestaurantType type
                                               , @RequestBody CreateReviewDTO createReviewDTO) {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(createReviewDTO.getRating())
                .title(createReviewDTO.getTitle())
                .content(createReviewDTO.getContent())
                .build();
        reviewService.createRestaurantReview(reviewDTO, type, memberId);

        return ResponseEntity.ok("Review Created");
    }

    @PostMapping("/createReview/dietFood/{dietFoodId}/{memberId}")
        public ResponseEntity<String> createDietFoodReview(@PathVariable("dietFoodId") Long dietFoodId,
                                                           @PathVariable("memberId") Long memberId,
                                                           @RequestBody CreateReviewDTO createReviewDTO) {

            ReviewDTO reviewDTO = ReviewDTO.builder()
                    .rating(createReviewDTO.getRating())
                    .title(createReviewDTO.getTitle())
                    .content(createReviewDTO.getContent())
                    .build();
            reviewService.createDietFoodReview(reviewDTO, dietFoodId, memberId);

            return ResponseEntity.ok("Review Created");
        }

    //특정 리뷰 수정
    @PutMapping("/{id}/modify")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        reviewService.modifyReview(reviewId, reviewDTO);
        return ResponseEntity.ok("Review Updated");
    }

    //특정 리뷰 삭제
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId) {
        reviewService.deleteReview(reviewId);
        log.info("삭제된 id : " + reviewId);
        return ResponseEntity.ok("Review Deleted");
    }

    //기숙사 식당 리뷰 조회
    @GetMapping("/dormitory")
        public List<ReviewDTO> dormitoryReviews() {
            return reviewService.findAllDormitoryReviews();
    }

}
