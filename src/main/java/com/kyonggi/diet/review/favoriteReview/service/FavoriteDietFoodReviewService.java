package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.repository.DietFoodReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FavoriteDietFoodReviewService
        extends AbstractFavoriteReviewService<FavoriteDietFoodReview> implements FavoriteReviewService {

    private final FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository;
    private final DietFoodReviewRepository dietFoodReviewRepository;

    FavoriteDietFoodReviewService(
            MemberService memberService,
            ModelMapper modelMapper,
            FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository,
            DietFoodReviewRepository dietFoodReviewRepository
    ) {
        super(memberService, modelMapper);
        this.favoriteDietFoodReviewRepository = favoriteDietFoodReviewRepository;
        this.dietFoodReviewRepository = dietFoodReviewRepository;
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.DORMITORY;
    }

    /**
     * 관심 음식 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param email (String)
     */
    @Transactional
    @Override
    public void createFavoriteReview(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            try {
                FavoriteDietFoodReview review = FavoriteDietFoodReview.builder()
                        .dietFoodReview(findOne(reviewId))
                        .member(member)
                        .build();
                favoriteDietFoodReviewRepository.save(review);
            } catch (Exception e) {
                throw new RuntimeException("관심 음식 리뷰 생성 실패. " + e.getMessage(), e);
            }
        } else {
            throw new IllegalStateException("이미 좋아요를 한 상태입니다");
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 삭제
     * @param email (String)
     * @param reviewId (Long)
     */
    @Transactional
    @Override
    public void deleteFavoriteReview(String email, Long reviewId) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        Long favoriteReviewId = validateThisIsMine(member, reviewId);

        if (favoriteReviewId == null) {
            throw new NoSuchElementException("좋아요한 리뷰가 없습니다");
        }

        try {
            favoriteDietFoodReviewRepository.deleteById(favoriteReviewId);
        } catch (Exception e) {
            throw new RuntimeException("관심 음식 리뷰 삭제 실패", e);
        }
    }

    /**
     * 해당 리뷰에 대한 좋아요 수 카운트 추출
     * @param reviewId (Long)
     * @return Long
     */
    @Override
    public Long getFavoriteReviewCountByReviewId(Long reviewId) {
        if (reviewId == null) throw new IllegalArgumentException("리뷰 ID가 null 입니다");
        try {
            Long count = favoriteDietFoodReviewRepository.getCountOfFavorite(reviewId);
            return (count != null) ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("리뷰 좋아요 수 가져오기 실패", e);
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 DTO 조회 메서드
     * @param email (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @Override
    public List<FavoriteDietFoodReviewDTO> findFavoriteReviewListByMember(String email) {

        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        List<FavoriteDietFoodReview> reviews = favoriteDietFoodReviewRepository.findFavoriteDietFoodReviewListByMember(member);
        if (reviews.isEmpty())
            return new ArrayList<>();

        return reviews.stream()
                .map(review -> {
                    FavoriteDietFoodReviewDTO dto = Objects.requireNonNull(modelMapper).map(review, FavoriteDietFoodReviewDTO.class);
                    dto.setMemberDTO(mapToMemberDTO(member));
                    dto.setDietFoodReviewId(review.getDietFoodReview().getId());
                    return dto;
                }).collect(Collectors.toList());
    }

    /** 멤버가 해당 리뷰에 이미 좋아요 눌렀는지 확인 */
    @Override
    protected Long validateThisIsMine(MemberEntity member, Long reviewId) {
        try {
            return favoriteDietFoodReviewRepository.validateThisIsMine(member, reviewId);
        } catch (Exception e) {
            throw new RuntimeException("좋아요 눌렀는지 확인하기 실패", e);
        }
    }

    @Override
    public FavoriteDietFoodReviewDTO findById(Long id) {
        return favoriteDietFoodReviewRepository.findById(id)
            .map(entity -> Objects.requireNonNull(modelMapper).map(entity, FavoriteDietFoodReviewDTO.class))
            .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + id));
    }

    private DietFoodReview findOne(Long reviewId) {
        return dietFoodReviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + reviewId));
    }
}
