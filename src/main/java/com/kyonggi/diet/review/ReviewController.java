package com.kyonggi.diet.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    //private final MemberService memberService;

    //리뷰 홈 페이지
    @GetMapping("/")
    public String reviewHome() {
        return "review_home";
    }

    //리뷰 한개 (리뷰 클릭시 경로에 reviewId)
    @GetMapping("/{id}")
    @ResponseBody
    public Review oneReview(@PathVariable("id") Long reviewId) {
        return reviewService.findReview(reviewId);
    }

    //모든 리뷰
    @GetMapping("/allReview")
    @ResponseBody
    public List<Review> allReview() {
        return reviewService.findAllReview();
    }

    //리뷰 생성
    /*@PostMapping("/createReview")
    public ResponseEntity<Review> createReview(@PathVariable("id") Long memberId, @RequestBody ReviewDTO reviewDTO) {
        Review newReview = Review.createReview(memberService.findOne(memberId), reviewDTO.getRating(), reviewDTO.getTitle(), reviewDTO.getContent());
        reviewService.saveReview(newReview);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    //특정 리뷰 수정
    @PostMapping("/{id}/modify")
    public String modifyReview(@PathVariable("id") Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        Review findReview = reviewService.findReview(reviewId);
        reviewService.modifyReview(findReview, reviewDTO);
        log.info("수정된 아이디 : " + reviewId);
        return "redirect:/api/review/" + reviewId;
    }

    //특정 리뷰 삭제
    @GetMapping("/{id}/delete")
    public ResponseEntity<Review> deleteReview(@PathVariable("id") Long reviewId) {
        reviewService.deleteReview(reviewService.findReview(reviewId));
        log.info("삭제된 id : " + reviewId);
        return new ResponseEntity<>(HttpStatus.OK);//"redirect:/api/review/";
    }

}
