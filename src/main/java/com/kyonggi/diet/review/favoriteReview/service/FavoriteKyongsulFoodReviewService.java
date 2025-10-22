package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteKyongsulFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteKyongsulFoodReviewRepository;
import com.kyonggi.diet.review.repository.KyongsulFoodReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FavoriteKyongsulFoodReviewService
        extends AbstractFavoriteReviewService<FavoriteKyongsulFoodReview> implements FavoriteReviewService {

    private final FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository;
    private final KyongsulFoodReviewRepository kyongsulFoodReviewRepository;

    FavoriteKyongsulFoodReviewService (
            MemberService memberService,
            ModelMapper modelMapper,
            FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository,
            KyongsulFoodReviewRepository kyongsulFoodReviewRepository
    ) {
        super(memberService, modelMapper);
        this.favoriteKyongsulFoodReviewRepository = favoriteKyongsulFoodReviewRepository;
        this.kyongsulFoodReviewRepository = kyongsulFoodReviewRepository;
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.KYONGSUL;
    }

    /** 관심 음식 리뷰 생성 메서드 */
    @Transactional
    @Override
    public void createFavoriteReview(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            try {
                FavoriteKyongsulFoodReview favorite = FavoriteKyongsulFoodReview.builder()
                        .kyongsulFoodReview(findOne(reviewId))
                        .member(member)
                        .build();
                favoriteKyongsulFoodReviewRepository.save(favorite);
            } catch (Exception e) {
                throw new RuntimeException("경슐랭 음식 리뷰 좋아요 생성 실패", e);
            }
        } else {
            throw new IllegalStateException("이미 좋아요를 한 상태입니다");
        }
    }

    /** 멤버별 관심 음식 리뷰 삭제 */
    @Transactional
    @Override
    public void deleteFavoriteReview(String email, Long reviewId) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        Long favoriteReviewId = validateThisIsMine(member, reviewId);

        if (favoriteReviewId == null) {
            throw new NoSuchElementException("좋아요한 리뷰가 없습니다");
        }

        try {
            favoriteKyongsulFoodReviewRepository.deleteById(favoriteReviewId);
        } catch (Exception e) {
            throw new RuntimeException("관심 경슐랭 음식 리뷰 삭제 실패", e);
        }
    }

    /** 좋아요 수 카운트 */
    @Override
    public Long getFavoriteReviewCountByReviewId(Long reviewId) {
        if (reviewId == null) throw new IllegalArgumentException("리뷰 ID가 null 입니다");
        try {
            Long count = favoriteKyongsulFoodReviewRepository.getCountOfFavorite(reviewId);
            return (count != null) ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("리뷰 좋아요 수 가져오기 실패", e);
        }
    }

    /** 멤버별 관심 리뷰 리스트 조회 */
    @Override
    public List<FavoriteKyongsulFoodReviewDTO> findFavoriteReviewListByMember(String email) {
        try {
            MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
            List<FavoriteKyongsulFoodReview> reviews = favoriteKyongsulFoodReviewRepository.findFavoriteKyongsulFoodReviewListByMember(member);
            if (reviews.isEmpty()) throw new NoSuchElementException("No Favorite Kyongsul Reviews found for member: " + email);

            return reviews.stream()
                    .map(review -> {
                        FavoriteKyongsulFoodReviewDTO dto = Objects.requireNonNull(modelMapper).map(review, FavoriteKyongsulFoodReviewDTO.class);
                        dto.setMemberDTO(mapToMemberDTO(member));
                        dto.setKyongsulFoodReviewId(review.getKyongsulFoodReview().getId());
                        return dto;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find favorite Kyongsul reviews by member. Email: {}", email, e);
            throw new RuntimeException("Failed to find favorite Kyongsul reviews by member.", e);
        }
    }

    /** 좋아요 유효성 검사 */
    @Override
    protected Long validateThisIsMine(MemberEntity member, Long reviewId) {
        try {
            return favoriteKyongsulFoodReviewRepository.validateThisIsMine(member, reviewId);
        } catch (Exception e) {
            throw new RuntimeException("좋아요 눌렀는지 확인 실패", e);
        }
    }

    @Override
    public FavoriteKyongsulFoodReviewDTO findById(Long id) {
        return favoriteKyongsulFoodReviewRepository.findById(id)
                .map(entity -> Objects.requireNonNull(modelMapper).map(entity, FavoriteKyongsulFoodReviewDTO.class))
                .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + id));
    }

    private KyongsulFoodReview findOne(Long reviewId) {
        return kyongsulFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + reviewId));
    }
}
