package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.ReviewSortType;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteESquareFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteESquareFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.service.FavoriteESquareFoodReviewService;
import com.kyonggi.diet.review.image.ReviewImageCommitService;
import com.kyonggi.diet.review.image.domain.ESquareFoodReviewImage;
import com.kyonggi.diet.review.image.dto.ReviewImageDTO;
import com.kyonggi.diet.review.image.repository.ESquareFoodReviewImageRepository;
import com.kyonggi.diet.review.moderation.block.BlockService;
import com.kyonggi.diet.review.repository.ESquareFoodReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ESquareFoodReviewService
        extends AbstractReviewService<ESquareFoodReview, Long>
        implements ReviewService<ESquareFoodReview> {

    private final ESquareFoodReviewRepository esquareFoodReviewRepository;
    private final ESquareFoodRepository esquareFoodRepository;
    private final FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository;
    private final BlockService blockService;
    private final ESquareFoodReviewImageRepository esquareFoodReviewImageRepository;
    private final ReviewImageCommitService reviewImageCommitService;

    ESquareFoodReviewService(
            ModelMapper modelMapper,
            MemberService memberService,
            ESquareFoodReviewRepository esquareFoodReviewRepository,
            ESquareFoodRepository eSquareFoodRepository,
            FavoriteESquareFoodReviewService favoriteESquareFoodReviewService,
            FavoriteESquareFoodReviewRepository favoriteESquareFoodReviewRepository,
            BlockService blockService,
            ESquareFoodReviewImageRepository esquareFoodReviewImageRepository,
            ReviewImageCommitService reviewImageCommitService) {
        super(memberService, modelMapper);
        this.esquareFoodReviewRepository = esquareFoodReviewRepository;
        this.esquareFoodRepository = eSquareFoodRepository;
        this.favoriteESquareFoodReviewRepository = favoriteESquareFoodReviewRepository;
        this.blockService = blockService;
        this.esquareFoodReviewImageRepository = esquareFoodReviewImageRepository;
        this.reviewImageCommitService = reviewImageCommitService;
    }

    public ESquareFoodReview getReview(Long reviewId) {
        return esquareFoodReviewRepository.findById(reviewId).orElseThrow(
                () -> new NoSuchElementException("해당 id에 대한 리뷰가 없습니다: " + reviewId));
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
        food.getESquareFoodReviews().add(review);

        saveNewImages(review, dto.getImageKeys());
    }

    private void saveNewImages(ESquareFoodReview review, List<String> tmpKeys) {
        List<String> committedKeys = reviewImageCommitService.commitTmpImages(tmpKeys);
        if (committedKeys.isEmpty()) return;

        List<ESquareFoodReviewImage> existing =
                esquareFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(review.getId());
        int startSortOrder = existing.isEmpty()
                ? 0
                : existing.get(existing.size() - 1).getSortOrder() + 1;

        List<ESquareFoodReviewImage> images = new ArrayList<>();
        for (int i = 0; i < committedKeys.size(); i++) {
            images.add(ESquareFoodReviewImage.builder()
                    .review(review)
                    .imageKey(committedKeys.get(i))
                    .sortOrder(startSortOrder + i)
                    .build());
        }
        esquareFoodReviewImageRepository.saveAll(images);
    }

    @Override
    public ReviewDTO findReviewDTO(Long id, CustomUserDetails user) {
        ESquareFoodReview review = esquareFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + id));
        return super.mapToReviewDTO(review, user);
    }

    @Override
    @Transactional
    public void modifyReview(Long reviewId, CreateReviewDTO dto) {
        ESquareFoodReview review = esquareFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        review.updateReview(dto.getRating(), dto.getTitle(), dto.getContent());

        deleteRemovedImages(reviewId, dto.getKeepImageIds());
        saveNewImages(review, dto.getImageKeys());
    }

    private void deleteRemovedImages(Long reviewId, List<Long> keepImageIds) {
        List<ESquareFoodReviewImage> existing =
                esquareFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId);

        Set<Long> keepIds = keepImageIds == null ? Set.of() : new HashSet<>(keepImageIds);
        List<ESquareFoodReviewImage> toDelete = existing.stream()
                .filter(image -> !keepIds.contains(image.getId()))
                .toList();
        if (toDelete.isEmpty()) return;

        reviewImageCommitService.deleteReviewImages(
                toDelete.stream().map(ESquareFoodReviewImage::getImageKey).toList());
        esquareFoodReviewImageRepository.deleteAllInBatch(toDelete);
    }

    @Override
    protected List<ReviewImageDTO> loadImages(ESquareFoodReview review) {
        return esquareFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(review.getId()).stream()
                .map(image -> ReviewImageDTO.builder()
                        .id(image.getId())
                        .url(reviewImageCommitService.generateViewUrl(image.getImageKey()))
                        .build())
                .toList();
    }

    @Override
    public List<String> getImageKeys(Long reviewId) {
        return esquareFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId).stream()
                .map(ESquareFoodReviewImage::getImageKey)
                .toList();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        List<ESquareFoodReviewImage> images =
                esquareFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId);
        reviewImageCommitService.deleteReviewImages(
                images.stream().map(ESquareFoodReviewImage::getImageKey).toList());

        esquareFoodReviewRepository.deleteById(reviewId);
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

    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo, ReviewSortType sort, CustomUserDetails user) {
        Pageable pageable = super.buildPageable(pageNo, sort);
        Page<ESquareFoodReview> reviews;

        if (user == null) {
            reviews = esquareFoodReviewRepository.findAllByESquareFoodId(foodId, pageable);
            return super.toPagedDTO(reviews, pageNo, null);
        }

        List<Long> blockedIds = blockService.getBlockedMemberIds(user.getMemberId());

        if (blockedIds.isEmpty()) {
            reviews = esquareFoodReviewRepository.findAllByESquareFoodId(foodId, pageable);
        } else {
            reviews = esquareFoodReviewRepository
                    .findAllExcludeBlocked(foodId, blockedIds, pageable);
        }

        return super.toPagedDTO(reviews, pageNo, user);
    }
}
