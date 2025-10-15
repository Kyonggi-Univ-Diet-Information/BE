package com.kyonggi.diet.member.service;

import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteKyongsulFoodReviewRepository;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {
    private final MemberService memberService;
    private final DietFoodReviewService dietFoodReviewService;
    private final KyongsulFoodReviewService kyongsulFoodReviewService;

    public MyPageDTO getMyPage(String email) {
        MemberEntity member = memberService.getMemberByEmail(email);
        List<ReviewDTO> dr = dietFoodReviewService.findAllByMember(member);
        List<ReviewDTO> kr = kyongsulFoodReviewService.findAllByMember(member);
        List<ReviewDTO> fdr = dietFoodReviewService.findAllByMemberFavorited(member);
        List<ReviewDTO> fkr = kyongsulFoodReviewService.findAllByMemberFavorited(member);

        return MyPageDTO.builder()
                .name(member.getName())
                .email(email)
                .dietFoodReviews(dr)
                .kyongsulReviews(kr)
                .favoriteDietFoodReviews(fdr)
                .favoriteKyongsulReviews(fkr)
                .build();
    }
}
