package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.DietFoodReviewControllerDocs;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/review/diet-food")
@Slf4j
@Tag(name = "음식 리뷰 API", description = "음식에 대한 리뷰 API 입니다. (조회, 생성, 삭제, 수정)")
public class DietFoodReviewController implements DietFoodReviewControllerDocs {

    private final DietFoodReviewService dietFoodReviewService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomMembersDetailService cd;

    /**
     * 음식 리뷰 1개 조회
     * @param reviewId (Long)
     * @return ReviewDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> oneReview(@PathVariable("id") Long reviewId) {
        try {
            ReviewDTO reviewDTO = dietFoodReviewService.findReview(reviewId);
            return ResponseEntity.ok(reviewDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Review not found with id: " + reviewId);
        }
    }

    /**
     * 모든 음식 리뷰 조회
     * @return List<ReviewDTO>
     */
    @GetMapping("/all")
    public ResponseEntity<?> allReview() {
        try {
            List<ReviewDTO> reviews = dietFoodReviewService.findAllReview();
            return ResponseEntity.ok(reviews);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Can't found reviews.");
        }
    }

    /**
     * 페이징된 리뷰 조회
     * @param pageNo (int)
     * @return Page<ReviewDTO>
     */
    @GetMapping("/all/paged/{dietFoodId}")
    public ResponseEntity<?> getPagedDietFoodReviews(@PathVariable("dietFoodId") Long dietFoodId,
                                                    @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo) {
        try {
            Page<ReviewDTO> reviewDTOS = dietFoodReviewService.getAllReviewsByFoodIdPaged(dietFoodId, pageNo);
            if (reviewDTOS.getContent().isEmpty())
                return ResponseEntity.ok(Page.empty().getContent());
            return ResponseEntity.ok(dietFoodReviewService.getAllReviewsByFoodIdPaged(dietFoodId, pageNo));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.ok(Page.empty().getContent());
        }
    }

    @GetMapping("/all/{dietFoodId}")
    public ResponseEntity<?> allReviewsById(@PathVariable("dietFoodId") Long dietFoodId) {
        List<ReviewDTO> findReview = new ArrayList<>();
        if (dietFoodReviewService.findListById(dietFoodId) == null) {
            return ResponseEntity.ok(findReview);
        }
        List<ReviewDTO> reviews = dietFoodReviewService.findListById(dietFoodId);
        return ResponseEntity.ok(reviews);
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
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or malformed");
            }

            String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));

            ReviewDTO reviewDTO = ReviewDTO.builder()
                    .rating(createReviewDTO.getRating())
                    .title(createReviewDTO.getTitle())
                    .content(createReviewDTO.getContent())
                    .build();

            dietFoodReviewService.createDietFoodReview(reviewDTO, dietFoodId, email);
            return ResponseEntity.ok("Review Created");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT expired: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (io.jsonwebtoken.SignatureException e) {
            log.error("Invalid JWT signature: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unhandled error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
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

        if (!dietFoodReviewService.verifyMember(reviewId, email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("You are not the author of this review.");
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("You are not the author of this review.");
        }

        dietFoodReviewService.deleteReview(reviewId);
        log.info("삭제된 id : " + reviewId);
        return ResponseEntity.ok("Review Deleted");
    }

    /**
     * 음식 리뷰 평점 구하기
     * @param dietFoodId (Long)
     * @return Double
     */
    @GetMapping("/average/{dietFoodId}")
    public ResponseEntity<?> getAverageRating(@PathVariable("dietFoodId") Long dietFoodId) {
        Double averageRating = dietFoodReviewService.findAverageRatingByDietFoodId(dietFoodId);
        if (averageRating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No ratings available for the given diet food ID.");
        }
        return ResponseEntity.ok(averageRating);
    }

    /**
     * 특정 음식에 대한 평점별 리뷰 개수 (1~5까지, 없는 평점은 0)
     * @param dietFoodId (Long)
     */
    @GetMapping("/rating-count/{dietFoodId}")
    public ResponseEntity<RatingCountResponse> getRatingCount(@PathVariable("dietFoodId") Long dietFoodId) {
        Map<Integer, Long> ratingCounts = dietFoodReviewService.getCountEachRating(dietFoodId);
        RatingCountResponse ratingCountResponse = new RatingCountResponse(ratingCounts);
        return ResponseEntity.ok(ratingCountResponse);
    }
}
