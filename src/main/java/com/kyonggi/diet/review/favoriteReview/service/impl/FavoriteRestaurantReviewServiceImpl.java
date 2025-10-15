package com.kyonggi.diet.review.favoriteReview.service.impl;

import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.favoriteReview.DTO.FavoriteRestaurantReviewDTO;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteRestaurantReviewRepository;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteRestaurantReviewService;
import com.kyonggi.diet.review.service.RestaurantReviewService;
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
public class FavoriteRestaurantReviewServiceImpl implements FavoriteRestaurantReviewService {

    private final FavoriteRestaurantReviewRepository favoriteRestaurantReviewRepository;
    private final RestaurantReviewService restaurantReviewService;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    /**
     * 관심 식당 리뷰 저장 메서드
     * @param favoriteRestaurantReview (FavoriteRestaurantReview)
     */
    @Override
    @Transactional
    public void save(FavoriteRestaurantReview favoriteRestaurantReview) {
        favoriteRestaurantReviewRepository.save(favoriteRestaurantReview);
    }

    /**
     * 관심 식당 리뷰 생성 메서드
     * @param reviewId (Long)
     * @param email (String)
     */
    @Override
    @Transactional
    public void createFavoriteRestaurantReview(Long reviewId, String email) {
        MemberEntity member = memberService.getMemberByEmail(email);

        if (validateThisIsMine(member, reviewId) == null) {
            FavoriteRestaurantReview review = FavoriteRestaurantReview.builder()
                    .restaurantReview(restaurantReviewService.findOne(reviewId))
                    .member(member)
                    .build();
            save(review);
            return;
        }

        throw new IllegalStateException("이미 좋아요를 한 상태입니다");
    }

    /**
     * 관심 식당 리뷰 엔티티 조회
     * @param id (Long)
     * @return FavoriteRestaurantReview
     */
    @Override
    public FavoriteRestaurantReview findOne(Long id) {
        return favoriteRestaurantReviewRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("No found Favorite Restaurant Review"));
    }

    /**
     * 관심 식당 리뷰 DTO 조회
     * @param id (Long)
     * @return FavoriteRestaurantReviewDTO
     */
    @Override
    public FavoriteRestaurantReviewDTO findById(Long id) {
        FavoriteRestaurantReview review = findOne(id);
        MemberEntity member = review.getMember();
        MemberDTO memberDTO = MemberDTO.builder()
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .password(member.getPassword())
                .profileUrl(member.getProfileUrl())
                .build();
        FavoriteRestaurantReviewDTO dto = mapToFavoriteRestaurantReviewDTO(review);
        dto.setMemberDTO(memberDTO);
        return dto;
    }

    /**
     * 관심 식당 리뷰 DTO 조회
     * @return List<FavoriteRestaurantReviewDTO>
     */
    @Override
    public List<FavoriteRestaurantReviewDTO> findAll() {
        List<FavoriteRestaurantReview> allReview = favoriteRestaurantReviewRepository.findAll();
        if (allReview.isEmpty())
            throw new NoSuchElementException("No found Favorite Restaurant Review List");

        return allReview.stream()
                .map(review -> {
                    MemberEntity member = review.getMember();
                    MemberDTO memberDTO = MemberDTO.builder()
                            .name(member.getName())
                            .email(member.getEmail())
                            .createdAt(member.getCreatedAt())
                            .password(member.getPassword())
                            .profileUrl(member.getProfileUrl())
                            .build();
                    FavoriteRestaurantReviewDTO dto = mapToFavoriteRestaurantReviewDTO(review);
                    dto.setMemberDTO(memberDTO);
                    return dto;
                }
            ).collect(Collectors.toList());
    }

    /**
     * 멤버별 관심 식당 리뷰 DTO 전체 조회
     * @param email (String)
     * @return List<FavoriteRestaurantReviewDTO>
     */
    @Override
    public List<FavoriteRestaurantReviewDTO> findFavoriteRestaurantReviewListByMember(String email) {
        MemberEntity member = memberService.getMemberByEmail(email);
        List<FavoriteRestaurantReview> reviews =
                favoriteRestaurantReviewRepository.findFavoriteRestaurantReviewListByMember(member);
        if (reviews.isEmpty()) {
            throw new NoSuchElementException("No found Favorite Restaurant Review By member");
        }
        return reviews.stream().map(review -> {
            MemberDTO memberDTO = MemberDTO.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .createdAt(member.getCreatedAt())
                    .password(member.getPassword())
                    .profileUrl(member.getProfileUrl())
                    .build();
            FavoriteRestaurantReviewDTO favoriteRestaurantReviewDTO = mapToFavoriteRestaurantReviewDTO(review);
            favoriteRestaurantReviewDTO.setMemberDTO(memberDTO);
            return favoriteRestaurantReviewDTO;
        }).collect(Collectors.toList());
    }

    private FavoriteRestaurantReviewDTO mapToFavoriteRestaurantReviewDTO(FavoriteRestaurantReview favoriteRestaurantReview) {
        return modelMapper.map(favoriteRestaurantReview, FavoriteRestaurantReviewDTO.class);
    }

    /**
     * 멤버별 관심 식당 리뷰 삭제
     * @param email    (String)
     * @param reviewId (Long)
     */
    @Transactional
    public void deleteFavoriteReview(String email, Long reviewId) {
        MemberEntity member = memberService.getMemberByEmail(email);
        Long favoriteReviewId  = validateThisIsMine(member, reviewId);

        if (favoriteReviewId == null) {
            throw new NoSuchElementException("좋아요한 리뷰가 없습니다");
        }

        favoriteRestaurantReviewRepository.delete(findOne(favoriteReviewId));
    }

    /**
     * 멤버가 해당 리뷰에 이미 좋아요 눌렀는지 확인
     * @param member   (MemberEntity)
     * @param reviewId (Long)
     * @return Long
     */
    private Long validateThisIsMine(MemberEntity member, Long reviewId) {
        return favoriteRestaurantReviewRepository.validateThisIsMine(member, reviewId);
    }
}
