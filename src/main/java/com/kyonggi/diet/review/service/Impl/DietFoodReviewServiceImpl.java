package com.kyonggi.diet.review.service.Impl;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodRepository;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import com.kyonggi.diet.review.repository.DietFoodReviewRepository;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DietFoodReviewServiceImpl implements DietFoodReviewService {

    private final DietFoodReviewRepository dietFoodReviewRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final DietFoodRepository dietFoodRepository;
    private final ModelMapper modelMapper;

    /**
     * 음식 리뷰 생성 메서드
     * @param reviewDTO (ReviewDTO)
     * @param dietFoodId (Long)
     * @param email (String)
     */
    @Override
    @Transactional
    public void createDietFoodReview(ReviewDTO reviewDTO, Long dietFoodId, String email) {
        DietFood dietFood = dietFoodRepository.findById(dietFoodId)
                .orElseThrow(() -> new NoSuchElementException("No found Diet food"));
        MemberEntity member = memberService.getMemberByEmail(email);

        DietFoodReview dietFoodReview = DietFoodReview.builder()
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .dietFood(dietFood)
                .member(member)
                .build();
        saveReview(dietFoodReview);

        dietFood.getDietFoodReviews().add(dietFoodReview);
        dietFoodRepository.save(dietFood);
    }

    /**
     * 음식 Review 엔티티 조회
     * @param id (Long)
     * @return DietFoodReview
     */
    @Override
    public DietFoodReview findOne(Long id) {
        return dietFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found with id: " + id));
    }

    /**
     * 저장 메서드
     * @param dietFoodReview (DietFoodReview)
     * @return id (Long)
     */
    @Override
    @Transactional
    public Long saveReview(DietFoodReview dietFoodReview) {
        dietFoodReviewRepository.save(dietFoodReview);
        return dietFoodReview.getId();
    }

    /**
     * 음식 Review DTO 조회
     * @param id (Long)
     * @return ReviewDTO
     */
    @Override
    public ReviewDTO findReview(Long id) {
        DietFoodReview review = findOne(id);
        ReviewDTO dto = mapToReviewDTO(review);
        dto.setMemberName(review.getMember().getName());
        return dto;
    }

    /**
     * 음식 Review DTO 리스트 조회
     * @return List<ReviewDTO>
     */
    @Override
    public List<ReviewDTO> findAllReview() {
        List<DietFoodReview> dietFoodReviews = dietFoodReviewRepository.findAll();
        if (dietFoodReviews.isEmpty()) {
            throw new EntityNotFoundException("Can't find reviews");
        }
        return dietFoodReviews.stream().map(review -> {
            ReviewDTO reviewDTO = mapToReviewDTO(review);
            reviewDTO.setMemberName(review.getMember().getName());
            return reviewDTO;
        }).collect(Collectors.toList());
    }

    public List<ReviewDTO> findListById(Long dietFoodId) {
        List<DietFoodReview> dietFoodReviews = dietFoodReviewRepository.findListById(dietFoodId);
        if (dietFoodReviews.isEmpty()) {
            throw new EntityNotFoundException("Can't find reviews");
        }
        return dietFoodReviews.stream().map(review -> {
            ReviewDTO reviewDTO = mapToReviewDTO(review);
            reviewDTO.setMemberName(review.getMember().getName());
            return reviewDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 음식 리뷰 수정 메서드
     * @param reviewId  (Long)
     * @param reviewDTO (ReviewDTO)
     */
    @Override
    @Transactional
    public void modifyReview(Long reviewId, ReviewDTO reviewDTO) {
        DietFoodReview dietFoodReview = findOne(reviewId);
        dietFoodReview.updateReview(reviewDTO.getRating(), reviewDTO.getTitle(), reviewDTO.getContent());
        saveReview(dietFoodReview);
    }

    /**
     * 음식 리뷰 삭제 메서드
     * @param id (Long)
     */
    @Transactional
    @Override
    public void deleteReview(Long id) {
        dietFoodReviewRepository.delete(findOne(id));
    }

    /**
     * 리뷰 작성자가 멤버가 맞는 지 확인
     * @param reviewId (Long)
     * @param email    (String)
     * @return boolean
     */
    @Override
    public boolean verifyMember(Long reviewId, String email) {
        MemberEntity member = memberService.getMemberByEmail(email);
        DietFoodReview review = findOne(reviewId);
        return member.getId().equals(review.getMember().getId());
    }


    /**
     * Review -> ReviewDTO
     * @param dietFoodReview (DietFoodReview)
     * @return ReviewDTO (ReviewDTO)
     */
    private ReviewDTO mapToReviewDTO(DietFoodReview dietFoodReview) {
        return modelMapper.map(dietFoodReview, ReviewDTO.class);
    }
}
