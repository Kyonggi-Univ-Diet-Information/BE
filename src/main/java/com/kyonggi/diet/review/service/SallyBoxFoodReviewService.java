package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.elasticsearch.service.MenuSearchService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.SallyBoxFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteSallyBoxFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteSallyBoxFoodReviewRepository;
import com.kyonggi.diet.review.repository.SallyBoxFoodReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SallyBoxFoodReviewService
        extends AbstractReviewService<SallyBoxFoodReview, Long> implements ReviewService {

    private final SallyBoxFoodReviewRepository sallyBoxFoodReviewRepository;
    private final SallyBoxFoodRepository sallyBoxFoodRepository;
    private final FavoriteSallyBoxFoodReviewRepository favoriteSallyBoxFoodReviewRepository;
    private final MenuSearchService menuSearchService;

    SallyBoxFoodReviewService(
            ModelMapper modelMapper,
            MemberService memberService,
            SallyBoxFoodReviewRepository sallyBoxFoodReviewRepository,
            SallyBoxFoodRepository sallyBoxFoodRepository,
            FavoriteSallyBoxFoodReviewRepository favoriteSallyBoxFoodReviewRepository, MenuSearchService menuSearchService) {
        super(memberService, modelMapper);
        this.sallyBoxFoodReviewRepository = sallyBoxFoodReviewRepository;
        this.sallyBoxFoodRepository = sallyBoxFoodRepository;
        this.favoriteSallyBoxFoodReviewRepository = favoriteSallyBoxFoodReviewRepository;
        this.menuSearchService = menuSearchService;
    }


    @Override
    protected JpaRepository<SallyBoxFoodReview, Long> getRepository() {
        return sallyBoxFoodReviewRepository;
    }

    @Override
    protected List<SallyBoxFoodReview> findAllReviewsByMember(MemberEntity member) {
        return sallyBoxFoodReviewRepository.findAllByMember(member);
    }

    @Override
    protected List<SallyBoxFoodReview> extractFavoritedReviews(MemberEntity member) {
        return favoriteSallyBoxFoodReviewRepository.findAllByMember(member).stream()
                .map(FavoriteSallyBoxFoodReview::getSallyBoxFoodReview)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.SALLY_BOX;
    }

    @Override
    @Transactional
    public void createReview(ReviewDTO dto, Long foodId, String email) {
        SallyBoxFood food = sallyBoxFoodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("No found E-Square Food"));
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        SallyBoxFoodReview review = SallyBoxFoodReview.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .rating(dto.getRating())
                .sallyBoxFood(food)
                .member(member)
                .build();

        sallyBoxFoodReviewRepository.save(review);
        menuSearchService.updateRating(foodId, (long) this.getReviewCount(foodId), this.getAverageRating(foodId));
        food.getSallyBoxFoodReviews().add(review);
    }

    @Override
    public ReviewDTO findReviewDTO(Long id) {
        SallyBoxFoodReview review = sallyBoxFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + id));
        return super.mapToReviewDTO(review);
    }

    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<SallyBoxFoodReview> reviews = sallyBoxFoodReviewRepository.findAllBySallyBoxFoodId(foodId, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    @Transactional
    public void modifyReview(Long reviewId, CreateReviewDTO dto) {
        SallyBoxFoodReview review = sallyBoxFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        review.updateReview(dto.getRating(), dto.getTitle(), dto.getContent());
        menuSearchService.updateRating(review.getSallyBoxFood().getId(), (long) this.getReviewCount(review.getSallyBoxFood().getId()), this.getAverageRating(review.getSallyBoxFood().getId()));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        sallyBoxFoodReviewRepository.deleteById(reviewId);
        SallyBoxFoodReview review = sallyBoxFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        menuSearchService.updateRating(review.getSallyBoxFood().getId(), (long) this.getReviewCount(review.getSallyBoxFood().getId()), this.getAverageRating(review.getSallyBoxFood().getId()));
    }

    @Override
    public boolean verifyMember(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        SallyBoxFoodReview review = sallyBoxFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        return member.getId().equals(review.getMember().getId());
    }

    @Override
    public Double getAverageRating(Long foodId) {
        return super.calculateAverage(sallyBoxFoodReviewRepository.findAverageRatingBySallyBoxFoodId(foodId));
    }

    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        return super.mergeRatingCounts(sallyBoxFoodReviewRepository.findRatingCountBySallyBoxFoodId(foodId));
    }

    @Override
    public int getReviewCount(Long foodId) {
        return sallyBoxFoodReviewRepository.getSallyBoxReviewCount(foodId);
    }

    @Override
    public List<ForTopReviewDTO> getRecentTop5() {
        return super.mapTopReviewResults(sallyBoxFoodReviewRepository.find5SallyBoxFoodReviewsRecent(PageRequest.of(0, 5)));
    }

    @Override
    public List<ForTopReviewDTO> getTop5ByRating() {
        return super.mapTopReviewResults(favoriteSallyBoxFoodReviewRepository.findTop5SallyBoxByMostFavorited(PageRequest.of(0, 5)));
    }

    @Override
    public Long extractId(SallyBoxFoodReview review) {
        return review.getId();
    }

    @Override
    public List<ReviewDTO> getAllReviews(Long id) {
        List<SallyBoxFoodReview> all = sallyBoxFoodReviewRepository.findListById(id);
        return all.stream().map(super::mapToReviewDTO).toList();
    }

    @Override
    public Page<ReviewDTO> findAllByMemberPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<SallyBoxFoodReview> reviews = sallyBoxFoodReviewRepository.findAllByMember(member, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    public Page<ReviewDTO> findAllByMemberFavoritedPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FavoriteSallyBoxFoodReview> favorites =
                favoriteSallyBoxFoodReviewRepository.findAllByMember(member, pageable);

        Page<SallyBoxFoodReview> reviews = favorites.map(FavoriteSallyBoxFoodReview::getSallyBoxFoodReview);
        return super.toPagedDTO(reviews, pageNo);
    }

}
