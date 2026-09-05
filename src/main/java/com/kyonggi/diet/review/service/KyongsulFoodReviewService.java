package com.kyonggi.diet.review.service;

import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.DietFoodRepository;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.ReviewSortType;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteKyongsulFoodReviewRepository;
import com.kyonggi.diet.review.image.ReviewImageCommitService;
import com.kyonggi.diet.review.image.domain.KyongsulFoodReviewImage;
import com.kyonggi.diet.review.image.dto.ReviewImageDTO;
import com.kyonggi.diet.review.image.repository.KyongsulFoodReviewImageRepository;
import com.kyonggi.diet.review.moderation.block.BlockService;
import com.kyonggi.diet.review.repository.KyongsulFoodReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class KyongsulFoodReviewService
        extends AbstractReviewService<KyongsulFoodReview, Long>
        implements ReviewService<KyongsulFoodReview> {

    private final KyongsulFoodReviewRepository kyongsulFoodReviewRepository;
    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository;
    private final BlockService blockService;
    private final KyongsulFoodReviewImageRepository kyongsulFoodReviewImageRepository;
    private final ReviewImageCommitService reviewImageCommitService;

    public KyongsulFoodReviewService(
            MemberService memberService,
            ModelMapper modelMapper,
            KyongsulFoodReviewRepository kyongsulFoodReviewRepository,
            FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository,
            KyongsulFoodRepository kyongsulFoodRepository,
            BlockService blockService,
            KyongsulFoodReviewImageRepository kyongsulFoodReviewImageRepository,
            ReviewImageCommitService reviewImageCommitService
    ) {
        super(memberService, modelMapper); // 상위 클래스 주입
        this.kyongsulFoodRepository = kyongsulFoodRepository;
        this.favoriteKyongsulFoodReviewRepository = favoriteKyongsulFoodReviewRepository;
        this.kyongsulFoodReviewRepository = kyongsulFoodReviewRepository;
        this.blockService = blockService;
        this.kyongsulFoodReviewImageRepository = kyongsulFoodReviewImageRepository;
        this.reviewImageCommitService = reviewImageCommitService;
    }

    public KyongsulFoodReview getReview(Long reviewId) {
        return kyongsulFoodReviewRepository.findById(reviewId).orElseThrow(
                () -> new NoSuchElementException("해당 id에 대한 리뷰가 없습니다: " + reviewId));
    }

    @Override
    protected JpaRepository<KyongsulFoodReview, Long> getRepository() {
        return kyongsulFoodReviewRepository;
    }

    @Override
    protected List<KyongsulFoodReview> findAllReviewsByMember(MemberEntity member) {
        return kyongsulFoodReviewRepository.findAllByMember(member);
    }

    @Override
    protected List<KyongsulFoodReview> extractFavoritedReviews(MemberEntity member) {
        return favoriteKyongsulFoodReviewRepository.findAllByMember(member).stream()
                .map(FavoriteKyongsulFoodReview::getKyongsulFoodReview)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public RestaurantType getRestaurantType() {
        return RestaurantType.KYONGSUL;
    }

    @Override
    @Transactional
    public void createReview(ReviewDTO dto, Long foodId, String email) {
        KyongsulFood food = kyongsulFoodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("No found KyongsulFood"));
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);

        KyongsulFoodReview review = KyongsulFoodReview.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .rating(dto.getRating())
                .kyongsulFood(food)
                .member(member)
                .build();

        kyongsulFoodReviewRepository.save(review);
        food.getKyongsulFoodReviews().add(review);

        saveNewImages(review, dto.getImageKeys());
    }

    private void saveNewImages(KyongsulFoodReview review, List<String> tmpKeys) {
        List<String> committedKeys = reviewImageCommitService.commitTmpImages(tmpKeys);
        if (committedKeys.isEmpty()) return;

        List<KyongsulFoodReviewImage> existing =
                kyongsulFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(review.getId());
        int startSortOrder = existing.isEmpty()
                ? 0
                : existing.get(existing.size() - 1).getSortOrder() + 1;

        List<KyongsulFoodReviewImage> images = new ArrayList<>();
        for (int i = 0; i < committedKeys.size(); i++) {
            images.add(KyongsulFoodReviewImage.builder()
                    .review(review)
                    .imageKey(committedKeys.get(i))
                    .sortOrder(startSortOrder + i)
                    .build());
        }
        kyongsulFoodReviewImageRepository.saveAll(images);
    }

    @Override
    public ReviewDTO findReviewDTO(Long id, CustomUserDetails user) {
        KyongsulFoodReview review = kyongsulFoodReviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + id));
        return super.mapToReviewDTO(review, user);
    }

    @Override
    @Transactional
    public void modifyReview(Long reviewId, CreateReviewDTO dto) {
        KyongsulFoodReview review = kyongsulFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        review.updateReview(dto.getRating(), dto.getTitle(), dto.getContent());

        deleteRemovedImages(reviewId, dto.getKeepImageIds());
        saveNewImages(review, dto.getImageKeys());
    }

    private void deleteRemovedImages(Long reviewId, List<Long> keepImageIds) {
        List<KyongsulFoodReviewImage> existing =
                kyongsulFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId);

        Set<Long> keepIds = keepImageIds == null ? Set.of() : new HashSet<>(keepImageIds);
        List<KyongsulFoodReviewImage> toDelete = existing.stream()
                .filter(image -> !keepIds.contains(image.getId()))
                .toList();
        if (toDelete.isEmpty()) return;

        reviewImageCommitService.deleteReviewImages(
                toDelete.stream().map(KyongsulFoodReviewImage::getImageKey).toList());
        kyongsulFoodReviewImageRepository.deleteAllInBatch(toDelete);
    }

    @Override
    protected List<ReviewImageDTO> loadImages(KyongsulFoodReview review) {
        return kyongsulFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(review.getId()).stream()
                .map(image -> ReviewImageDTO.builder()
                        .id(image.getId())
                        .url(reviewImageCommitService.generateViewUrl(image.getImageKey()))
                        .build())
                .toList();
    }

    @Override
    public List<String> getImageKeys(Long reviewId) {
        return kyongsulFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId).stream()
                .map(KyongsulFoodReviewImage::getImageKey)
                .toList();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        List<KyongsulFoodReviewImage> images =
                kyongsulFoodReviewImageRepository.findAllByReview_IdOrderBySortOrderAsc(reviewId);
        reviewImageCommitService.deleteReviewImages(
                images.stream().map(KyongsulFoodReviewImage::getImageKey).toList());

        kyongsulFoodReviewRepository.deleteById(reviewId);
    }

    @Override
    public boolean verifyMember(Long reviewId, String email) {
        MemberEntity member = Objects.requireNonNull(memberService).getMemberByEmail(email);
        KyongsulFoodReview review = kyongsulFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("No review found"));
        return member.getId().equals(review.getMember().getId());
    }

    @Override
    public Double getAverageRating(Long foodId) {
        return super.calculateAverage(kyongsulFoodReviewRepository.findAverageRatingByKyongsulFoodId(foodId));
    }

    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        return super.mergeRatingCounts(kyongsulFoodReviewRepository.findRatingCountByKyongsulFoodId(foodId));
    }

    @Override
    public int getReviewCount(Long foodId) {
        return kyongsulFoodReviewRepository.getKyongsulReviewCount(foodId);
    }

    @Override
    public List<ForTopReviewDTO> getRecentTop5() {
        return super.mapTopReviewResults(kyongsulFoodReviewRepository.find5KyongsulFoodReviewsRecent(PageRequest.of(0, 5)));
    }

    @Override
    public List<ForTopReviewDTO> getTop5ByRating() {
        return super.mapTopReviewResults(favoriteKyongsulFoodReviewRepository.findTop5KyongsulByMostFavorited(PageRequest.of(0, 5)));
    }

    @Override
    public Long extractId(KyongsulFoodReview review) {
        return review.getId();
    }

    @Override
    public Page<ReviewDTO> findAllByMemberPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<KyongsulFoodReview> reviews = kyongsulFoodReviewRepository.findAllByMember(member, pageable);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    public Page<ReviewDTO> findAllByMemberFavoritedPaged(MemberEntity member, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<FavoriteKyongsulFoodReview> favorites =
                favoriteKyongsulFoodReviewRepository.findAllByMember(member, pageable);

        Page<KyongsulFoodReview> reviews = favorites.map(FavoriteKyongsulFoodReview::getKyongsulFoodReview);
        return super.toPagedDTO(reviews, pageNo);
    }

    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo, ReviewSortType sort, CustomUserDetails user) {
        Pageable pageable = super.buildPageable(pageNo, sort);
        Page<KyongsulFoodReview> reviews;

        if (user == null) {
            reviews = kyongsulFoodReviewRepository.findAllByKyongsulFoodId(foodId, pageable);
            return super.toPagedDTO(reviews, pageNo, null);
        }

        List<Long> blockedIds = blockService.getBlockedMemberIds(user.getMemberId());

        if (blockedIds.isEmpty()) {
            reviews = kyongsulFoodReviewRepository.findAllByKyongsulFoodId(foodId, pageable);
        } else {
            reviews = kyongsulFoodReviewRepository
                    .findAllExcludeBlocked(foodId, blockedIds, pageable);
        }

        return super.toPagedDTO(reviews, pageNo, user);
    }
}
