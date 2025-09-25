package com.kyonggi.diet.review.favoriteReview.service.impl;

import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteKyongsulFoodReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteKyongsulFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteKyongsulFoodReviewService;
import com.kyonggi.diet.review.repository.KyongsulFoodReviewRepository;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FavoriteKyongsulFoodReviewServiceImpl implements FavoriteKyongsulFoodReviewService {

    private final FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository;
    private final MemberService memberService;
    private final KyongsulFoodReviewService kyongsulFoodReviewService;
    private final KyongsulFoodReviewRepository kyongsulFoodReviewRepository;
    private final ModelMapper modelMapper;

    /**
     * 관심 음식 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param email    (String)
     */
    @Override
    @Transactional
    public void createFavoriteKyongsulReview(Long reviewId, String email) {
        MemberEntity member = memberService.getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            try {
                KyongsulFoodReview review = kyongsulFoodReviewRepository.findById(reviewId).orElse(null);
                FavoriteKyongsulFoodReview favorite = FavoriteKyongsulFoodReview.builder()
                        .kyongsulFoodReview(review)
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

    /**
     * 관심 음식 리뷰 DTO 조회 메서드
     *
     * @param id (Long)
     * @return FavoriteKyongsulFoodReviewDTO
     */
    @Override
    public FavoriteKyongsulFoodReviewDTO findById(Long id) {
        try {
            FavoriteKyongsulFoodReview review = findOne(id);
            MemberEntity member = review.getMember();
            MemberDTO memberDTO = MemberDTO.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .createdAt(member.getCreatedAt())
                    .password(member.getPassword())
                    .profileUrl(member.getProfileUrl())
                    .build();
            FavoriteKyongsulFoodReviewDTO dto = mapToFavoriteKyongsulFoodReviewDTO(review);
            dto.setMemberDTO(memberDTO);
            dto.setKyongsulFoodReviewId(review.getKyongsulFoodReview().getId());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("관심 경슐랭 음식 리뷰 dto 찾기 실패.", e);
        }
    }

    /**
     * 관심 경슐랭 음식 리뷰 DTO 전체 조회 메서드
     *
     * @return List<FavoriteKyongsulFoodReviewDTO>
     */
    @Override
    public List<FavoriteKyongsulFoodReviewDTO> findAll() {
        try {
            List<FavoriteKyongsulFoodReview> reviews = favoriteKyongsulFoodReviewRepository.findAll();
            if (reviews.isEmpty()) {
                throw new NoSuchElementException("No Favorite kyongsul food Reviews found.");
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
                FavoriteKyongsulFoodReviewDTO dto = mapToFavoriteKyongsulFoodReviewDTO(review);
                dto.setMemberDTO(memberDTO);
                dto.setKyongsulFoodReviewId(review.getKyongsulFoodReview().getId());
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("관심 경슐랭 음식 리뷰 dto 전체 조회 실패", e);
        }
    }

    /**
     * 몀버별 관심 음식 리뷰 DTO 조회 메서드
     *
     * @param email (String)
     * @return List<FavoriteKyongsulFoodReviewDTO>
     */
    @Override
    public List<FavoriteKyongsulFoodReviewDTO> findFavoriteKyongsulFoodReviewListByMember(String email) {
        try {
            MemberEntity member = memberService.getMemberByEmail(email);
            List<FavoriteKyongsulFoodReview> reviews = favoriteKyongsulFoodReviewRepository.findFavoriteKyongsulFoodReviewListByMember(member);
            if (reviews.isEmpty()) {
                throw new NoSuchElementException("No Favorite kyongsul food Reviews found for member with email: " + email);
            }
            return reviews.stream().map(review -> {
                MemberDTO memberDTO = MemberDTO.builder()
                        .name(member.getName())
                        .email(member.getEmail())
                        .createdAt(member.getCreatedAt())
                        .password(member.getPassword())
                        .profileUrl(member.getProfileUrl())
                        .build();
                FavoriteKyongsulFoodReviewDTO dto = mapToFavoriteKyongsulFoodReviewDTO(review);
                dto.setMemberDTO(memberDTO);
                dto.setKyongsulFoodReviewId(review.getKyongsulFoodReview().getId());
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find favorite kyongsul food reviews by member. Email: {}", email, e);
            throw new RuntimeException("Failed to find favorite kyongsul food reviews by member.", e);
        }
    }

    /**
     * 멤버별 관심 음식 리뷰 삭제
     * @param email    (String)
     * @param reviewId (Long)
     */
    @Transactional
    @Override
    public void deleteFavoriteReview(String email, Long reviewId) {
        MemberEntity member = memberService.getMemberByEmail(email);
        Long favoriteReviewId = validateThisIsMine(member, reviewId);

        if (favoriteReviewId == null) {
            throw new NoSuchElementException("좋아요한 리뷰가 없습니다");
        }

        try {
            favoriteKyongsulFoodReviewRepository.delete(findOne(favoriteReviewId));
        } catch (Exception e) {
            throw new RuntimeException("관심 경슐랭 음식 리뷰 삭제 실패", e);
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
            Long count = favoriteKyongsulFoodReviewRepository.getCountOfFavorite(reviewId);
            return (count != null) ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("리뷰 좋아요 수 가져오기 실패", e);
        }
    }

    /**
     * 관심 음식 리뷰 엔티티 조회 메서드
     * @param id (Long)
     * @return FavoriteKyongsulFoodReview
     */
    private FavoriteKyongsulFoodReview findOne(Long id) {
        return favoriteKyongsulFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No found Favorite DietFood Review : " + id));
    }

    /**
     * 멤버가 해당 리뷰에 이미 좋아요 눌렀는지 확인
     * @param member   (MemberEntity)
     * @param reviewId (Long)
     * @return Long
     */
    private Long validateThisIsMine(MemberEntity member, Long reviewId) {
        try {
            return favoriteKyongsulFoodReviewRepository.validateThisIsMine(member, reviewId);
        } catch (Exception e) {
            throw new RuntimeException("좋아요 눌렀는지 확인하기 실패", e);
        }
    }

    private FavoriteKyongsulFoodReviewDTO mapToFavoriteKyongsulFoodReviewDTO(FavoriteKyongsulFoodReview favoriteKyongsulFoodReview) {
        return modelMapper.map(favoriteKyongsulFoodReview, FavoriteKyongsulFoodReviewDTO.class);
    }
}
