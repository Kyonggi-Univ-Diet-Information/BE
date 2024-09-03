package com.kyonggi.diet.review.service.Impl;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.restaurant.service.RestaurantService;
import com.kyonggi.diet.review.Review;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.ReviewRepository;
import com.kyonggi.diet.review.service.ReviewService;
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
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final RestaurantService restaurantService;
    private final DietFoodService dietFoodService;
    private final ModelMapper modelMapper;

    /**
     * Review 엔티티 조회
     * @param id (Long)
     * @return Review
     */
    @Override
    public Review findOne(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Review not found with id: " + id));
    }

    /**
     * 저장 메서드
     * @param review (Review)
     */
    @Override
    @Transactional
    public Long saveReview(Review review) {
        reviewRepository.save(review);
        return review.getId();
    }

    /**
     * 멤버별 Restaurant 리뷰 저장
     * @param reviewDTO (ReviewDTO)
     * @param memberId (Long)
     */
    @Override
    @Transactional
    public void createRestaurantReview(ReviewDTO reviewDTO, RestaurantType type, Long memberId) {
        Restaurant restaurant = restaurantService.findRestaurantByType(type);
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(()-> new NoSuchElementException("No found Member"));

        Review review = Review.builder()
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .restaurant(restaurant)
                .member(member)
                .build();
        saveReview(review);

        restaurant.getReviews().add(review);
        restaurantService.save(restaurant);
    }

    @Override
    @Transactional
    public void createDietFoodReview(ReviewDTO reviewDTO, Long dietFoodId, Long memberId) {
        DietFood dietFood = dietFoodService.findOne(dietFoodId);
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(()-> new NoSuchElementException("No member found"));

        Review review = Review.builder()
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .dietFood(dietFood)
                .member(member)
                .build();
        saveReview(review);

        dietFood.getReviews().add(review);
        dietFoodService.save(dietFood);
    }

    /**
     * Review DTO 조회
     * @param id (Long)
     * @return ReviewDTO
     */
    @Override
    public ReviewDTO findReview(Long id) {
        return mapToReviewDTO(findOne(id));
    }

    /**
     * Review DTO 리스트 조회
     * @return List<ReviewDTO>
     */
    @Override
    public List<ReviewDTO> findAllReview() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
                    throw new EntityNotFoundException("Can't find reviews");
        }
        return  reviews.stream()
                .map(this::mapToReviewDTO)
                .toList();
    }

    /**
     * 리뷰 수정 메서드
     * @param reviewId (Long)
     * @param reviewDTO (ReviewDTO)
     */
    @Override
    @Transactional
    public void modifyReview(Long reviewId, ReviewDTO reviewDTO) {
        Review review = findOne(reviewId);
        review.updateReview(reviewDTO.getRating(), reviewDTO.getTitle(), reviewDTO.getContent());
        saveReview(review);
    }

    /**
     * 리뷰 삭제 메서드
     * @param id (Long)
     */
    @Transactional
    @Override
    public void deleteReview(Long id) {
        reviewRepository.delete(findOne(id));
    }

    /**
     * 기숙사 식당 리뷰 DTO 조회
     * @return List<ReviewDTO>
     */
    @Override
    public List<ReviewDTO> findAllDormitoryReviews() {
        List<Review> dormitoryReviews = restaurantService.findDormitory().getReviews();
        if (dormitoryReviews.isEmpty()) {
            throw new EntityNotFoundException("Can't find Dormitory reviews");
        }
        return dormitoryReviews.stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Review -> ReviewDTO
     * @param review (review)
     * @return ReviewDTO (ReviewDTO)
     */
    private ReviewDTO mapToReviewDTO(Review review) {
            return modelMapper.map(review, ReviewDTO.class);
        }
}
