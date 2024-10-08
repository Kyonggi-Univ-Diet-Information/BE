package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/review/diet-food")
@Slf4j
public class DietFoodReviewController {

    private final DietFoodReviewService dietFoodReviewService;

    /**
     * 음식 리뷰 1개 조회
     * @param reviewId (Long)
     * @return ReviewDTO
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO oneReview(@PathVariable("id") Long reviewId) {
        return dietFoodReviewService.findReview(reviewId);
    }

    /**
     * 모든 음식 리뷰 조회
     * @return List<ReviewDTO>
     */
    @GetMapping("/all")
    @ResponseBody
    public List<ReviewDTO> allReview() {
        return dietFoodReviewService.findAllReview();
    }

    /**
     * 음식 리뷰 생성
     * @param dietFoodId (Long)
     * @param email (String)
     * @param createReviewDTO (CreateReviewDTO)
     * @return ResponseEntity
     */
    @PostMapping("/new/{dietFoodId}/{email}")
    public ResponseEntity<String> createDietFoodReview(@PathVariable("dietFoodId") Long dietFoodId,
                                                       @PathVariable("email") String email,
                                                       @RequestBody CreateReviewDTO createReviewDTO) {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(createReviewDTO.getRating())
                .title(createReviewDTO.getTitle())
                .content(createReviewDTO.getContent())
                .build();
        dietFoodReviewService.createDietFoodReview(reviewDTO, dietFoodId, email);

        return ResponseEntity.ok("Review Created");
    }


    /**
     * 음식 리뷰 수정
     * @param reviewId (Long)
     * @param email (String)
     * @param reviewDTO (ReviewDTO)
     * @return ResponseEntity
     */
    @PutMapping("/modify/{id}/{email}")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email,
                                               @RequestBody ReviewDTO reviewDTO) {
        if(!dietFoodReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

        dietFoodReviewService.modifyReview(reviewId, reviewDTO);
        return ResponseEntity.ok("Review Updated");
    }


    /**
     * 음식 리뷰 삭제
     * @param reviewId (Long)
     * @param email (String)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{id}/{email}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId,
                                               @PathVariable("email") String email) {
        if (!dietFoodReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

        dietFoodReviewService.deleteReview(reviewId);
        log.info("삭제된 id : " + reviewId);
        return ResponseEntity.ok("Review Deleted");
    }
}
