package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.DietFoodReviewControllerDocs;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "음식 리뷰 API", description = "음식에 대한 리뷰 API 입니다. (조회, 생성, 삭제, 수정)")
public class DietFoodReviewController implements DietFoodReviewControllerDocs {

    private final DietFoodReviewService dietFoodReviewService;
    private final JwtTokenUtil jwtTokenUtil;

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

    @GetMapping("/all/{dietFoodId}")
    @ResponseBody
    public List<ReviewDTO> allReviewsById(@PathVariable("dietFoodId") Long dietFoodId) {
        return dietFoodReviewService.findListById(dietFoodId);
    }

    /**
     * 음식 리뷰 생성
     * @param dietFoodId (Long)
     * @param token (String)
     * @param createReviewDTO (CreateReviewDTO)
     * @return ResponseEntity
     */
    @PostMapping("/new/{dietFoodId}")
    public ResponseEntity<String> createDietFoodReview(@PathVariable("dietFoodId") Long dietFoodId,
                                                       @RequestHeader("Authorization") String token,
                                                       @RequestBody CreateReviewDTO createReviewDTO) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

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
     * @param token (String)
     * @param reviewDTO (ReviewDTO)
     * @return ResponseEntity
     */
    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyReview(@PathVariable("id") Long reviewId,
                                               @RequestBody ReviewDTO reviewDTO,
                                               @RequestHeader("Authorization") String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        if(!dietFoodReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

        dietFoodReviewService.modifyReview(reviewId, reviewDTO);
        return ResponseEntity.ok("Review Updated");
    }


    /**
     * 음식 리뷰 삭제
     * @param reviewId (Long)
     * @param token (String)
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId,
                                               @RequestHeader("Authorization") String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        if (!dietFoodReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.ok("작성자가 아닙니다.");
        }

        dietFoodReviewService.deleteReview(reviewId);
        log.info("삭제된 id : " + reviewId);
        return ResponseEntity.ok("Review Deleted");
    }
}
