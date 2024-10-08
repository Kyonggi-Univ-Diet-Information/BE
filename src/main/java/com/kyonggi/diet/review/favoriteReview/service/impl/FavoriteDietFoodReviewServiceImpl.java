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
    private final MemberRepository memberRepository;
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
        favoriteDietFoodReviewRepository.save(favoriteDietFoodReview);
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
        FavoriteDietFoodReview review = FavoriteDietFoodReview.builder()
                .dietFoodReview(dietFoodReviewService.findOne(reviewId))
                .member(member)
                .build();
        save(review);
    }

    /**
     * 관심 음식 리뷰 엔티티 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReview
     */
    @Override
    public FavoriteDietFoodReview findOne(Long id) {
        return favoriteDietFoodReviewRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("No found Favorite DietFood Review"));
    }

    /**
     * 관심 음식 리뷰 DTO 조회 메서드
     * @param id (Long)
     * @return FavoriteDietFoodReviewDTO
     */
    @Override
    public FavoriteDietFoodReviewDTO findById(Long id) {
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
        return dto;
    }

    /**
     * 관심 음식 리뷰 DTO 전체 조회 메서드
     * @return List<FavoriteDietReviewDTO>
     */
    @Override
    public List<FavoriteDietFoodReviewDTO> findAll() {
        List<FavoriteDietFoodReview> reviews = favoriteDietFoodReviewRepository.findAll();
        if (reviews.isEmpty())
            throw new NoSuchElementException("No found Favorite DietFood Review List");
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
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 몀버별 관심 음식 리뷰 DTO 조회 메서드
     * @param email (String)
     * @return List<FavoriteDietFoodReviewDTO>
     */
    @Override
    public List<FavoriteDietFoodReviewDTO> findFavoriteDietFoodReviewListByMember(String email) {
        MemberEntity member = memberService.getMemberByEmail(email);
        List<FavoriteDietFoodReview> reviews =
                favoriteDietFoodReviewRepository.findFavoriteDietFoodReviewListByMember(member);
        if (reviews.isEmpty()) {
            throw new NoSuchElementException("No found Favorite Diet Food Review By member");
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
            return dto;
        }).collect(Collectors.toList());
    }

    private FavoriteDietFoodReviewDTO mapToFavoriteDietFoodReviewDTO(FavoriteDietFoodReview favoriteDietFoodReview) {
        return modelMapper.map(favoriteDietFoodReview, FavoriteDietFoodReviewDTO.class);

    }
}
