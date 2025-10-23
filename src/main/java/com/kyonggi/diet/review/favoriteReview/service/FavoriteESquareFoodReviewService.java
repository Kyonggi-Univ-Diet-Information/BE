package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteESquareFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteKyongsulFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteESquareFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteESquareFoodReviewRepository;
import com.kyonggi.diet.review.repository.ESquareFoodReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FavoriteESquareFoodReviewService
        extends AbstractFavoriteReviewService<FavoriteESquareFoodReview> implements FavoriteReviewService {

    private final FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository;
    private final ESquareFoodReviewRepository esquareFoodReviewRepository;

    FavoriteESquareFoodReviewService(
            MemberService memberService, ModelMapper modelMapper,
            FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository,
            ESquareFoodReviewRepository esquareFoodReviewRepository
    ) {
        super(memberService, modelMapper);
        this.esquareFoodReviewRepository = esquareFoodReviewRepository;
        this.favoriteESquareFoodReviewRepository = favoriteESquareFoodReviewRepository;
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.E_SQUARE;
    }

    /**
     * 관심 음식 리뷰 생성 메서드
     */
    @Transactional
    @Override
    public void createFavoriteReview(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            try {
                FavoriteESquareFoodReview favorite = FavoriteESquareFoodReview.builder()
                        .esquareFoodReview(findOne(reviewId))
                        .member(member)
                        .build();
                favoriteESquareFoodReviewRepository.save(favorite);
            } catch (Exception e) {
                throw new RuntimeException("이스퀘어 음식 리뷰 좋아요 생성 실패", e);
            }
        } else {
            throw new IllegalStateException("이미 좋아요를 한 상태입니다");
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 삭제
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
            favoriteESquareFoodReviewRepository.deleteById(favoriteReviewId);
        } catch (Exception e) {
            throw new RuntimeException("관심 이스퀘어 음식 리뷰 삭제 실패", e);
        }
    }

    /**
     * 좋아요 수 카운트
     */
    @Override
    public Long getFavoriteReviewCountByReviewId(Long reviewId) {
        if (reviewId == null) throw new IllegalArgumentException("리뷰 ID가 null 입니다");
        try {
            Long count = favoriteESquareFoodReviewRepository.getCountOfFavorite(reviewId);
            return (count != null) ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("리뷰 좋아요 수 가져오기 실패", e);
        }
    }

    /**
     * 멤버별 관심 리뷰 리스트 조회
     */
    @Override
    public List<FavoriteESquareFoodReviewDTO> findFavoriteReviewListByMember(String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        List<FavoriteESquareFoodReview> reviews = favoriteESquareFoodReviewRepository.findFavoriteESquareFoodReviewListByMember(member);
        if (reviews.isEmpty())
            return new ArrayList<>();

        return reviews.stream()
                .map(review -> {
                    FavoriteESquareFoodReviewDTO dto = Objects.requireNonNull(modelMapper).map(review, FavoriteESquareFoodReviewDTO.class);
                    dto.setMemberDTO(mapToMemberDTO(member));
                    dto.setESquareFoodReviewId(review.getEsquareFoodReview().getId());
                    return dto;
                }).collect(Collectors.toList());
    }

    /**
     * 좋아요 유효성 검사
     */
    @Override
    protected Long validateThisIsMine(MemberEntity member, Long reviewId) {
        try {
            return favoriteESquareFoodReviewRepository.validateThisIsMine(member, reviewId);
        } catch (Exception e) {
            throw new RuntimeException("좋아요 눌렀는지 확인 실패", e);
        }
    }

    @Override
    public FavoriteESquareFoodReviewDTO findById(Long id) {
        return favoriteESquareFoodReviewRepository.findById(id)
                .map(entity -> Objects.requireNonNull(modelMapper).map(entity, FavoriteESquareFoodReviewDTO.class))
                .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + id));
    }

    private ESquareFoodReview findOne(Long reviewId) {
        return esquareFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + reviewId));
    }
}
