package com.kyonggi.diet.review.service.Impl;

import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantRepository;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.restaurant.service.RestaurantService;
import com.kyonggi.diet.review.domain.RestaurantReview;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.repository.RestaurantReviewRepository;
import com.kyonggi.diet.review.service.RestaurantReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantReviewServiceImpl implements RestaurantReviewService {

    private final RestaurantReviewRepository restaurantReviewRepository;
    private final MemberRepository memberRepository;
    private final RestaurantService restaurantService;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    /**
     * 식당 Review 엔티티 조회
     * @param id (Long)
     * @return Review
     */
    @Override
    public RestaurantReview findOne(Long id) {
        return restaurantReviewRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Review not found with id: " + id));
    }

    /**
     * 식당 리뷰 저장 메서드
     * @param restaurantReview (Review)
     */
    @Override
    @Transactional
    public Long saveReview(RestaurantReview restaurantReview) {
        restaurantReviewRepository.save(restaurantReview);
        return restaurantReview.getId();
    }

    /**
     * 멤버별 Restaurant 리뷰 저장
     * @param reviewDTO (ReviewDTO)
     * @param email (String)
     */
    @Override
    @Transactional
    public void createRestaurantReview(ReviewDTO reviewDTO, RestaurantType type, String email) {
        Restaurant restaurant = restaurantService.findRestaurantByType(type);
        MemberEntity member = memberService.getMemberByEmail(email);

        RestaurantReview restaurantReview = RestaurantReview.builder()
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .restaurant(restaurant)
                .member(member)
                .build();
        saveReview(restaurantReview);

        restaurant.getRestaurantReviews().add(restaurantReview);
        restaurantService.save(restaurant);
    }



    /**
     * 식당 Review DTO 조회
     * @param id (Long)
     * @return ReviewDTO
     */
    @Override
    public ReviewDTO findReview(Long id) {
        RestaurantReview review = findOne(id);
        ReviewDTO dto = mapToReviewDTO(review);
        dto.setMemberName(review.getMember().getName());
        return dto;
    }

    /**
     * 식당 Review DTO 리스트 조회
     * @return List<ReviewDTO>
     */
    @Override
    public List<ReviewDTO> findAllReview() {
        List<RestaurantReview> restaurantReviews = restaurantReviewRepository.findAll();
        if (restaurantReviews.isEmpty()) {
                    throw new EntityNotFoundException("Can't find reviews");
        }
        return restaurantReviews.stream().map(review -> {
            ReviewDTO reviewDTO = mapToReviewDTO(review);
            reviewDTO.setMemberName(review.getMember().getName());
            return reviewDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 식당 리뷰 수정 메서드
     * @param reviewId (Long)
     * @param reviewDTO (ReviewDTO)
     */
    @Override
    @Transactional
    public void modifyReview(Long reviewId, ReviewDTO reviewDTO) {
        RestaurantReview restaurantReview = findOne(reviewId);
        restaurantReview.updateReview(reviewDTO.getRating(), reviewDTO.getTitle(), reviewDTO.getContent());
        saveReview(restaurantReview);
    }

    /**
     * 식당 리뷰 삭제 메서드
     * @param id (Long)
     */
    @Transactional
    @Override
    public void deleteReview(Long id) {
        restaurantReviewRepository.delete(findOne(id));
    }

    @Override
    public List<ReviewDTO> findReviewsByType(RestaurantType type) {
        Restaurant restaurant = restaurantService.findRestaurantByType(type);
        List<RestaurantReview> restaurantReviews = restaurantReviewRepository.findReviewsByRestaurant(restaurant);
        return restaurantReviews.stream().map(review -> ReviewDTO.builder()
                .title(review.getTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .memberName(review.getMember().getName()).build())
                .collect(Collectors.toList());
    }

    /**
     * Review -> ReviewDTO
     * @param restaurantReview (review)
     * @return ReviewDTO (ReviewDTO)
     */
    private ReviewDTO mapToReviewDTO(RestaurantReview restaurantReview) {
            return modelMapper.map(restaurantReview, ReviewDTO.class);
        }
}
