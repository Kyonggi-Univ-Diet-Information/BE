package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.domain.DietFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.DietFoodRepository;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.repository.DietFoodReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class DietFoodReviewService
        extends AbstractReviewService<DietFoodReview, Long>
        implements ReviewService<DietFoodReview> {

    private final DietFoodReviewRepository dietFoodReviewRepository;
    private final FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository;
    private final DietFoodRepository dietFoodRepository;

    public DietFoodReviewService(
            MemberService memberService,
            ModelMapper modelMapper,
            DietFoodReviewRepository dietFoodReviewRepository,
            FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository,
            DietFoodRepository dietFoodRepository
    ) {
        super(memberService, modelMapper); // 상위 클래스 주입
        this.dietFoodReviewRepository = dietFoodReviewRepository;
        this.favoriteDietFoodReviewRepository = favoriteDietFoodReviewRepository;
        this.dietFoodRepository = dietFoodRepository;
    }

    @Override
    public DietFoodReview getReview(Long reviewId) {
        return dietFoodReviewRepository.findById(reviewId).orElseThrow(
                () -> new NoSuchElementException("해당 id에 대한 리뷰가 없습니다: " + reviewId));
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.DORMITORY;
    }

    @Override
    protected JpaRepository<DietFoodReview, Long> getRepository() {
        return dietFoodReviewRepository;
    }

    @Override
    protected List<DietFoodReview> findAllReviewsByMember(MemberEntity member) {
        return dietFoodReviewRepository.findAllByMember(member);
    }

    /** 즐겨찾기 리뷰 추출 (상위에서 자동 DTO 변환 처리됨) */
    @Override
    protected List<DietFoodReview> extractFavoritedReviews(MemberEntity member) {
        return favoriteDietFoodReviewRepository.findAllByMember(member).stream()
                .map(FavoriteDietFoodReview::getDietFoodReview)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public void createReview(ReviewDTO dto, Long foodId, String email) {
        DietFood dietFood = dietFoodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("No found Diet food"));
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        DietFoodReview review = DietFoodReview.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .rating(dto.getRating())
                .dietFood(dietFood)
                .member(member)
                .build();

        dietFoodReviewRepository.save(review);
        dietFood.getDietFoodReviews().add(review);
    }

    @Override
    public ReviewDTO findReviewDTO(Long id, CustomUserDetails user) {
        DietFoodReview review = dietFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + id));
        return super.mapToReviewDTO(review, user);
    }

    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo, CustomUserDetails user) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<DietFoodReview> reviews = dietFoodReviewRepository.findAllByDietFoodId(foodId, pageable);
        return super.toPagedDTO(reviews, pageNo, user);
    }

    @Override
    @Transactional
    public void modifyReview(Long reviewId, CreateReviewDTO dto) {
        DietFoodReview review = dietFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        review.updateReview(dto.getRating(), dto.getTitle(), dto.getContent());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        dietFoodReviewRepository.deleteById(reviewId);
    }

    @Override
    public boolean verifyMember(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        DietFoodReview review = dietFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        return member.getId().equals(review.getMember().getId());
    }

    @Override
    public Double getAverageRating(Long foodId) {
        return super.calculateAverage(dietFoodReviewRepository.findAverageRatingByDietFoodId(foodId));
    }

    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        return super.mergeRatingCounts(dietFoodReviewRepository.findRatingCountByDietFoodId(foodId));
    }

    @Override
    public int getReviewCount(Long foodId) {
        return dietFoodReviewRepository.getDietFoodReviewCount(foodId);
    }

    @Override
    public List<ForTopReviewDTO> getRecentTop5() {
        return super.mapTopReviewResults(dietFoodReviewRepository.find5DietFoodReviewsRecent(PageRequest.of(0, 5)));
    }

    @Override
    public List<ForTopReviewDTO> getTop5ByRating() {
        return super.mapTopReviewResults(favoriteDietFoodReviewRepository.findTop5DietByMostFavorited(PageRequest.of(0, 5)));
    }

    @Override
    public Long extractId(DietFoodReview review) {
        return review.getId();
    }

    @Override
    public Page<ReviewDTO> findAllByMemberPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<DietFoodReview> reviews = dietFoodReviewRepository.findAllByMember(member, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    public Page<ReviewDTO> findAllByMemberFavoritedPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FavoriteDietFoodReview> favorites =
                favoriteDietFoodReviewRepository.findAllByMember(member, pageable);

        Page<DietFoodReview> reviews = favorites.map(FavoriteDietFoodReview::getDietFoodReview);
        return super.toPagedDTO(reviews, pageNo);
    }

}
