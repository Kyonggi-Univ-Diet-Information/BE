package com.kyonggi.diet.member.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MyPageRepository;
import com.kyonggi.diet.review.DTO.MyReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import com.kyonggi.diet.review.service.ESquareFoodReviewService;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
import com.kyonggi.diet.review.service.SallyBoxFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {
    private final MemberService memberService;
    private final DietFoodReviewService dietFoodReviewService;
    private final KyongsulFoodReviewService kyongsulFoodReviewService;
    private final ESquareFoodReviewService esquareFoodReviewService;
    private final SallyBoxFoodReviewService sallyBoxFoodReviewService;
    private final MyPageRepository myPageRepository;

    public MyPageDTO getMyPage(String email) {
        MemberEntity member = memberService.getMemberByEmail(email);

        return MyPageDTO.builder()
                .name(member.getName())
                .email(email)
                .createdAt(member.getCreatedAt().toString())
                .build();
    }

    /**
     * 내가 쓴 리뷰
     */
    public Page<ReviewDTO> getMyReviews(String email, RestaurantType type, int page) {
        var member = memberService.getMemberByEmail(email);
        Pageable pageable = PageRequest.of(page, 10);
        return switch (type) {
            case DORMITORY -> dietFoodReviewService.findAllByMemberPaged(member, page);
            case KYONGSUL -> kyongsulFoodReviewService.findAllByMemberPaged(member, page);
            case E_SQUARE -> esquareFoodReviewService.findAllByMemberPaged(member, page);
            case SALLY_BOX -> sallyBoxFoodReviewService.findAllByMemberPaged(member, page);
            default -> Page.empty(pageable);
        };
    }

    /**
     * 내가 좋아요한 리뷰
     */
    public Page<ReviewDTO> getMyFavoriteReviews(String email, RestaurantType type, int page) {
        var member = memberService.getMemberByEmail(email);
        Pageable pageable = PageRequest.of(page, 10);
        return switch (type) {
            case DORMITORY -> dietFoodReviewService.findAllByMemberFavoritedPaged(member, page);
            case KYONGSUL -> kyongsulFoodReviewService.findAllByMemberFavoritedPaged(member, page);
            case E_SQUARE -> esquareFoodReviewService.findAllByMemberFavoritedPaged(member, page);
            case SALLY_BOX -> sallyBoxFoodReviewService.findAllByMemberFavoritedPaged(member, page);
            default -> Page.empty(pageable);
        };
    }

    // 내가 쓴 리뷰
    public Page<MyReviewDTO> getMyWrittenReviews(String email, int page) {
        MemberEntity member = memberService.getMemberByEmail(email);
        PageRequest pageable = PageRequest.of(page, 10);
        return myPageRepository.findMyWrittenReviews(member.getId(), pageable);
    }

    // 내가 좋아요한 리뷰
    public Page<MyReviewDTO> getMyFavoritedReviews(String email, int page) {
        MemberEntity member = memberService.getMemberByEmail(email);
        PageRequest pageable = PageRequest.of(page, 10);
        return myPageRepository.findMyFavoritedReviews(member.getId(), pageable);
    }
}
