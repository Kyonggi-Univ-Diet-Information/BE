package com.kyonggi.diet.review.favoriteReview.service.impl;

import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteDietFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteDietFoodReviewService;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FavoriteDietFoodReviewServiceImpl implements FavoriteDietFoodReviewService {

    private final FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository;
    private final MemberService memberService;
    private final DietFoodReviewService dietFoodReviewService;
    private final ModelMapper modelMapper;

    /**
     * 관식 음식 리뷰 저장 메서드
     * @param favoriteDietFoodReview (FavoriteDietFoodReview)
     */
    @Override
    @Transactional
    public void save(FavoriteDietFoodReview favoriteDietFoodReview) {
        try {
            favoriteDietFoodReviewRepository.save(favoriteDietFoodReview);
        } catch (Exception e) {
            throw new RuntimeException("관심 음식 리뷰 저장 실패", e);
        }
    }

    /**
     * 관심 음식 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param email (String)
     */
    @Override
    @Transactional
    public void createFavoriteDietFoodReview(Long reviewId, String email) {
        MemberEntity member = memberService.getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            try {
                FavoriteDietFoodReview review = FavoriteDietFoodReview.builder()
                        .dietFoodReview(dietFoodReviewService.findOne(reviewId))
                        .member(member)
                        .build();
                save(review);
            } catch (Exception e) {
                throw new RuntimeException("관심 음식 리뷰 생성 실패", e);
            }
        } else {
            throw new IllegalStateException("이미 좋아요를 한 상태입니다");
        }
    }

    /**
     * 관심 음식 리뷰 엔티티 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReview
     */
    @Override
    public FavoriteDietFoodReview findOne(Long id) {
        return favoriteDietFoodReviewRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("No found Favorite DietFood Review : " + id));
    }

    /**
     * 관심 음식 리뷰 DTO 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReviewDTO
     */
    @Override
    public FavoriteDietFoodReviewDTO findById(Long id) {
        try {
            FavoriteDietFoodReview review = findOne(id);
            MemberEntity member = review.getMember();
            MemberDTO memberDTO = MemberDTO.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .createdAt(member.getCreatedAt())
                    .password(member.getPassword())
                    .profileUrl(member.getProfileUrl())
                    .build();
            FavoriteDietFoodReviewDTO dto = mapToFavoriteDietFoodReviewDTO(review);
            dto.setMemberDTO(memberDTO);
            dto.setDietFoodReviewId(review.getDietFoodReview().getId());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("관심 음식 리뷰 dto 찾기 실패.", e);
        }
    }

    /**
     * 관심 음식 리뷰 DTO 전체 조회 메서드
     * @return List<FavoriteDietReviewDTO>
     */
    @Override
    public List<FavoriteDietFoodReviewDTO> findAll() {
        try {
            List<FavoriteDietFoodReview> reviews = favoriteDietFoodReviewRepository.findAll();
            if (reviews.isEmpty()) {
                throw new NoSuchElementException("No Favorite DietFood Reviews found.");
            }
            return reviews.stream().map(review -> {
                MemberEntity member = review.getMember();
                MemberDTO memberDTO = MemberDTO.builder()
                        .name(member.getName())
                        .email(member.getEmail())
                        .createdAt(member.getCreatedAt())
                        .password(member.getPassword())
                        .profileUrl(member.getProfileUrl())
                        .build();
                FavoriteDietFoodReviewDTO dto = mapToFavoriteDietFoodReviewDTO(review);
                dto.setMemberDTO(memberDTO);
                dto.setDietFoodReviewId(review.getDietFoodReview().getId());
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("관심 음식 리뷰 dto 전체 조회 실패", e);
        }
    }

    /**
     * 몀버별 관심 음식 리뷰 DTO 조회 메서드
     * @param email (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @Override
    public List<FavoriteDietFoodReviewDTO> findFavoriteDietFoodReviewListByMember(String email) {
        try {
            MemberEntity member = memberService.getMemberByEmail(email);
            List<FavoriteDietFoodReview> reviews = favoriteDietFoodReviewRepository.findFavoriteDietFoodReviewListByMember(member);
            if (reviews.isEmpty()) {
                throw new NoSuchElementException("No Favorite DietFood Reviews found for member with email: " + email);
            }
            return reviews.stream().map(review -> {
                MemberDTO memberDTO = MemberDTO.builder()
                        .name(member.getName())
                        .email(member.getEmail())
                        .createdAt(member.getCreatedAt())
                        .password(member.getPassword())
                        .profileUrl(member.getProfileUrl())
                        .build();
                FavoriteDietFoodReviewDTO dto = mapToFavoriteDietFoodReviewDTO(review);
                dto.setMemberDTO(memberDTO);
                dto.setDietFoodReviewId(review.getDietFoodReview().getId());
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find favorite diet food reviews by member. Email: {}", email, e);
            throw new RuntimeException("Failed to find favorite diet food reviews by member.", e);
        }
    }

    private FavoriteDietFoodReviewDTO mapToFavoriteDietFoodReviewDTO(FavoriteDietFoodReview favoriteDietFoodReview) {
        return modelMapper.map(favoriteDietFoodReview, FavoriteDietFoodReviewDTO.class);
    }

    /**
     * 멤버별 관심 음식 리뷰 삭제
     * @param email (String)
     * @param reviewId (Long)
     */
    @Transactional
    @Override
    public void deleteFavoriteReview(String email, Long reviewId) {
        MemberEntity member = memberService.getMemberByEmail(email);
        Long favoriteReviewId  = validateThisIsMine(member, reviewId);

        if (favoriteReviewId == null) {
            throw new NoSuchElementException("좋아요한 리뷰가 없습니다");
        }

        try {
            favoriteDietFoodReviewRepository.delete(findOne(favoriteReviewId));
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
        if (reviewId == null) {
            throw new IllegalArgumentException("리뷰 ID가 null 입니다");
        }

        try {
            Long count = favoriteDietFoodReviewRepository.getCountOfFavorite(reviewId);
            return (count != null) ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("리뷰 좋아요 수 가져오기 실패", e);
        }
    }


    /**
     * 멤버가 해당 리뷰에 이미 좋아요 눌렀는지 확인
     * @param member (MemberEntity)
     * @param reviewId (Long)
     * @return Long
     */
    private Long validateThisIsMine(MemberEntity member, Long reviewId) {
        try {
            return favoriteDietFoodReviewRepository.validateThisIsMine(member, reviewId);
        } catch (Exception e) {
            throw new RuntimeException("좋아요 눌렀는지 확인하기 실패", e);
        }
    }
}
