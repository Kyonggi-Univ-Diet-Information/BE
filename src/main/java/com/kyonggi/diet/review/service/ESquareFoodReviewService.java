package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.elasticsearch.service.MenuSearchService;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteESquareFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteESquareFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteESquareFoodReviewService;
import com.kyonggi.diet.review.repository.ESquareFoodReviewRepository;
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
public class ESquareFoodReviewService
        extends AbstractReviewService<ESquareFoodReview, Long> implements ReviewService {

    private final ESquareFoodReviewRepository esquareFoodReviewRepository;
    private final ESquareFoodRepository esquareFoodRepository;
    private final FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository;
    private final MenuSearchService menuSearchService;

    ESquareFoodReviewService(
            ModelMapper modelMapper,
            MemberService memberService,
            ESquareFoodReviewRepository esquareFoodReviewRepository,
            ESquareFoodRepository eSquareFoodRepository,
            FavoriteESquareFoodReviewService favoriteESquareFoodReviewService,
            FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository, MenuSearchService menuSearchService) {
        super(memberService, modelMapper);
        this.esquareFoodReviewRepository = esquareFoodReviewRepository;
        this.esquareFoodRepository = eSquareFoodRepository;
        this.favoriteESquareFoodReviewRepository = favoriteESquareFoodReviewRepository;
        this.menuSearchService = menuSearchService;
    }


    @Override
    protected JpaRepository<ESquareFoodReview, Long> getRepository() {
        return esquareFoodReviewRepository;
    }

    @Override
    protected List<ESquareFoodReview> findAllReviewsByMember(MemberEntity member) {
        return esquareFoodReviewRepository.findAllByMember(member);
    }

    @Override
    protected List<ESquareFoodReview> extractFavoritedReviews(MemberEntity member) {
        return favoriteESquareFoodReviewRepository.findAllByMember(member).stream()
                .map(FavoriteESquareFoodReview::getEsquareFoodReview)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.E_SQUARE;
    }

    @Override
    @Transactional
    public void createReview(ReviewDTO dto, Long foodId, String email) {
        ESquareFood food = esquareFoodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("No found E-Square Food"));
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        ESquareFoodReview review = ESquareFoodReview.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .rating(dto.getRating())
                .eSquareFood(food)
                .member(member)
                .build();

        esquareFoodReviewRepository.save(review);
        menuSearchService.updateRating(foodId, (long) this.getReviewCount(foodId), this.getAverageRating(foodId));
        food.getESquareFoodReviews().add(review);
    }

    @Override
    public ReviewDTO findReviewDTO(Long id) {
        ESquareFoodReview review = esquareFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + id));
        return super.mapToReviewDTO(review);
    }

    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ESquareFoodReview> reviews = esquareFoodReviewRepository.findAllByESquareFoodId(foodId, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    @Transactional
    public void modifyReview(Long reviewId, CreateReviewDTO dto) {
        ESquareFoodReview review = esquareFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        review.updateReview(dto.getRating(), dto.getTitle(), dto.getContent());
        menuSearchService.updateRating(review.getESquareFood().getId(), (long) this.getReviewCount(review.getESquareFood().getId()), this.getAverageRating(review.getESquareFood().getId()));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        esquareFoodReviewRepository.deleteById(reviewId);
        ESquareFoodReview review = esquareFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        menuSearchService.updateRating(review.getESquareFood().getId(), (long) this.getReviewCount(review.getESquareFood().getId()), this.getAverageRating(review.getESquareFood().getId()));
    }

    @Override
    public boolean verifyMember(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        ESquareFoodReview review = esquareFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        return member.getId().equals(review.getMember().getId());
    }

    @Override
    public Double getAverageRating(Long foodId) {
        return super.calculateAverage(esquareFoodReviewRepository.findAverageRatingByESquareFoodId(foodId));
    }

    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        return super.mergeRatingCounts(esquareFoodReviewRepository.findRatingCountByESquareFoodId(foodId));
    }

    @Override
    public int getReviewCount(Long foodId) {
        return esquareFoodReviewRepository.getESquareReviewCount(foodId);
    }

    @Override
    public List<ForTopReviewDTO> getRecentTop5() {
        return super.mapTopReviewResults(esquareFoodReviewRepository.find5ESquareFoodReviewsRecent(PageRequest.of(0, 5)));
    }

    @Override
    public List<ForTopReviewDTO> getTop5ByRating() {
        return super.mapTopReviewResults(favoriteESquareFoodReviewRepository.findTop5ESquareByMostFavorited(PageRequest.of(0, 5)));
    }

    @Override
    public Long extractId(ESquareFoodReview review) {
            return review.getId();
        }

    @Override
    public List<ReviewDTO> getAllReviews(Long id) {
        List<ESquareFoodReview> all = esquareFoodReviewRepository.findListById(id);
        return all.stream().map(super::mapToReviewDTO).toList();
    }

    @Override
    public Page<ReviewDTO> findAllByMemberPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ESquareFoodReview> reviews = esquareFoodReviewRepository.findAllByMember(member, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    public Page<ReviewDTO> findAllByMemberFavoritedPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FavoriteESquareFoodReview> favorites =
                favoriteESquareFoodReviewRepository.findAllByMember(member, pageable);

        Page<ESquareFoodReview> reviews = favorites.map(FavoriteESquareFoodReview::getEsquareFoodReview);
        return super.toPagedDTO(reviews, pageNo);
    }

}
