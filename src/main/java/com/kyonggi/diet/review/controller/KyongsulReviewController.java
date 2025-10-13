package com.kyonggi.diet.review.controller;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.KyongsulReviewControllerDocs;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.RatingCountResponse;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/kyongsul-food")
@Slf4j
@CrossOrigin("*")
@Tag(name = "경슐랭 음식 리뷰 API", description = "경슐랭 음식에 대한 리뷰 API 입니다. (조회, 생성, 삭제, 수정)")
public class KyongsulReviewController implements KyongsulReviewControllerDocs {
    private final KyongsulFoodReviewService kyongsulFoodReviewService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 음식 리뷰 생성 API
     * @param foodId (Long)
     * @param token (String)
     * @param createReviewDTO (CreateReviewDTO)
     */
    @PostMapping("/new/{foodId}")
    public ResponseEntity<?> createReview(@PathVariable("foodId") Long foodId,
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

            kyongsulFoodReviewService.createKyongsulFoodReview(reviewDTO, foodId, email);
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
     * 리뷰 한개 조회 API
     * @param reviewId (Long)
     */
    @GetMapping("/one/{reviewId}")
    public ResponseEntity<?> getOne(@PathVariable("reviewId") Long reviewId) {
        try {
            return ResponseEntity.ok(kyongsulFoodReviewService.findReviewDTOById(reviewId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 id에 대한 리뷰 없음");
        }
    }

    /**
     * 페이징된 음식 리뷰 리스트 조회 API
     * @param foodId (Long)
     * @param pageNo (int)
     */
    @GetMapping("/paged/{foodId}")
    public ResponseEntity<?> getPage(@PathVariable("foodId") Long foodId,
                                     @RequestParam(required = false, defaultValue = "0", value = "pageNo") int pageNo) {
        try {
            Page<ReviewDTO> reviewDTOS = kyongsulFoodReviewService.getAllReviewsByFoodIdPaged(foodId, pageNo);
            if (reviewDTOS.getContent().isEmpty())
                return ResponseEntity.ok(Page.empty().getContent());
            return ResponseEntity.ok(kyongsulFoodReviewService.getAllReviewsByFoodIdPaged(foodId, pageNo));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.ok(Page.empty().getContent());
        }
    }

    /**
     * 경슐랭 음식 리뷰 삭제 API
     * @param reviewId (Long)
     * @param token (String)
     */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteOne(@PathVariable("reviewId") Long reviewId, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or malformed");
        }

        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        if (kyongsulFoodReviewService.verifyMember(reviewId, email)) {
            kyongsulFoodReviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * 경슐랭 음식 리뷰 수정 API
     * @param reviewId (Long)
     * @param token (String)
     * @param reviewDTO (CreateReviewDTO)
     */
    @PutMapping("/modify/{reviewId}")
    public ResponseEntity<?> modifyOne(@PathVariable("reviewId") Long reviewId,
                                       @RequestHeader("Authorization") String token,
                                       @RequestBody CreateReviewDTO reviewDTO) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or malformed");
        }

        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        if (kyongsulFoodReviewService.verifyMember(reviewId, email)) {
            kyongsulFoodReviewService.modifyKyongsulFoodReview(reviewId, reviewDTO);
            return ResponseEntity.ok("Review Modified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * 경슐랭 음식 리뷰에 대한 평점 평균 조회 API
     * @param kyongsulFoodId (Long)
     */
    @GetMapping("/average-rating/{kyongsulFoodId}")
    public Double getAverageRatingByKyongsulFoodId(@PathVariable("kyongsulFoodId") Long kyongsulFoodId) {
        return kyongsulFoodReviewService.getAverageRatingForReview(kyongsulFoodId);
    }

    /**
     * 특정 음식에 대한 평점별 리뷰 개수 (1~5까지, 없는 평점은 0)
     * @param kyongsulFoodId (Long)
     */
    @GetMapping("/rating-count/{kyongsulFoodId}")
    public ResponseEntity<RatingCountResponse> getRatingCount(@PathVariable("kyongsulFoodId") Long kyongsulFoodId) {
        Map<Integer, Long> ratingCounts = kyongsulFoodReviewService.getCountEachRating(kyongsulFoodId);
        RatingCountResponse ratingCountResponse = new RatingCountResponse(ratingCounts);
        return ResponseEntity.ok(ratingCountResponse);
    }
}
