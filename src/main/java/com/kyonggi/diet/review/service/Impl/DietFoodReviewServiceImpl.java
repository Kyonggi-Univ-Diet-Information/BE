package com.kyonggi.diet.review.service.Impl;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodRepository;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import com.kyonggi.diet.review.repository.DietFoodReviewRepository;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
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

    /**
     * 페이징된 ReivewDTO 조회
     * @param pageNo (Int)
     * @return Page<ReviewDTO>
     */
    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<DietFoodReview> all = dietFoodReviewRepository.findAllByDietFoodId(foodId, pageable);
        if (all.isEmpty()) {
            throw new EntityNotFoundException("Can't find Paged DietFood reviews");
        }
        return  all.map(this::mapToReviewDTO);
    }

    /**
     * 해당 음식에 대한 각 리뷰 rating 카운팅 개수 리턴  메서드
     * @param foodId (Long)
     * @return Map<Integer, Long>
     */
    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        List<Object[]> results = dietFoodReviewRepository.findRatingCountByDietFoodId(foodId);

        Map<Integer, Long> ratingCountMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCountMap.put(i, 0L);
        }

        for (Object[] result : results) {
            Double rating = (Double) result[0];
            Long count = (Long) result[1];
            ratingCountMap.put(rating.intValue(), count);
        }

        return ratingCountMap;
    }

    /**
     * 음식 id를 통해 리뷰 리스트 구하기
     * @param dietFoodId (Long)
     * @return List<ReviewDTO>
     */
    @Override
    public List<ReviewDTO> findListById(Long dietFoodId) {
        List<DietFoodReview> dietFoodReviews = dietFoodReviewRepository.findListById(dietFoodId);
        if (dietFoodReviews.isEmpty()) {
            return null;
        }
        return dietFoodReviews.stream().map(this::mapToReviewDTO).collect(Collectors.toList());
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
     * 해당 음식 리뷰에 대한 평점 구하기
     * @param dietFoodId (Long)
     * @return Double
     */
    @Override
    public Double findAverageRatingByDietFoodId(Long dietFoodId) {
        try {
            return dietFoodReviewRepository.findAverageRatingByDietFoodId(dietFoodId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * 기숙사 음식 각 음식에 대한 리뷰 카운팅
     * @param id (Long)
     */
    @Override
    public int findDietFoodReviewCount(Long id) {
        try {
            return dietFoodReviewRepository.getDietFoodReviewCount(id);
        }  catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * 기숙사 음식 리뷰 중 최신순 TOP 5 조회
     */
    @Override
    public List<ForTopReviewDTO> find5DietFoodReviewsRecent() {
        List<Object[]> results = dietFoodReviewRepository.find5DietFoodReviewsRecent(PageRequest.of(0, 5));

        return results.stream()
                .map(row -> {
                    Long memberId = ((Number) row[5]).longValue();
                    String memberName = memberService.getNameById(memberId);

                    return ForTopReviewDTO.builder()
                            .foodId(((Number) row[0]).longValue())
                            .reviewId(((Number) row[1]).longValue())
                            .rating(((Number) row[2]).doubleValue())
                            .title((String) row[3])
                            .content((String) row[4])
                            .memberName(memberName)
                            .build();
                })
                .toList();
    }

    /**
     * Review -> ReviewDTO
     * @param dietFoodReview (DietFoodReview)
     * @return ReviewDTO (ReviewDTO)
     */
    private ReviewDTO mapToReviewDTO(DietFoodReview dietFoodReview) {
        ReviewDTO dto = modelMapper.map(dietFoodReview, ReviewDTO.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        if (dietFoodReview.getCreatedAt() != null) {
            dto.setCreatedAt(dietFoodReview.getCreatedAt().toLocalDateTime().format(formatter));
        }
        if (dietFoodReview.getUpdatedAt() != null) {
            dto.setUpdatedAt(dietFoodReview.getUpdatedAt().toLocalDateTime().format(formatter));
        }

        return dto;
    }
}
