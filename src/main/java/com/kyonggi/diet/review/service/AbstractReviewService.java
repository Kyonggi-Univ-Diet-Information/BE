package com.kyonggi.diet.review.service;

import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.ForTopReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.Review;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 리뷰 관련 서비스의 공통 로직 상위 클래스
 * - Review → ReviewDTO 매핑
 * - 평균 평점 계산
 * - 평점별 카운트 계산
 * - 페이징 공통 처리
 * - TOP5 공통 매핑 처리
 */
@Transactional(readOnly = true)
@NoArgsConstructor(force = true)
public abstract class AbstractReviewService<R extends Review, ID> {

    protected final MemberService memberService;
    protected final ModelMapper modelMapper;

    protected AbstractReviewService(MemberService memberService, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }
    protected abstract ID extractId(R review);

    /** 하위 클래스에서 Repository 주입 */
    protected abstract JpaRepository<R, Long> getRepository();

    /**
     * 하위 클래스에서 Member 기준 조회 구현
     */
    protected abstract List<R> findAllReviewsByMember(MemberEntity member);
    protected abstract List<R> extractFavoritedReviews(MemberEntity member);

    /**
     * Review -> ReviewDTO
     * @param review (Review)
     * @return ReviewDTO
     */
    protected ReviewDTO mapToReviewDTO(R review, CustomUserDetails user) {
        ReviewDTO dto = Objects.requireNonNull(modelMapper).map(review, ReviewDTO.class);

        if (user != null) {
            dto.setMyReview(
                    review.getMember().getId().equals(user.getMemberId())
            );
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().toLocalDateTime().format(formatter));
        }
        if (review.getUpdatedAt() != null) {
            dto.setUpdatedAt(review.getUpdatedAt().toLocalDateTime().format(formatter));
        }
        return dto;
    }

    protected ReviewDTO mapToReviewDTO(R review) {
        ReviewDTO dto = Objects.requireNonNull(modelMapper).map(review, ReviewDTO.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().toLocalDateTime().format(formatter));
        }
        if (review.getUpdatedAt() != null) {
            dto.setUpdatedAt(review.getUpdatedAt().toLocalDateTime().format(formatter));
        }
        return dto;
    }

    /**
     * 공통 평점 평균 계산 메서드
     */
    protected Double calculateAverage(Double average) {
        try {
            return Math.floor(average * 10) / 10.0;
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    /**
     * 공통 평점 카운팅 Map
     */
    protected Map<Integer, Long> mergeRatingCounts(List<Object[]> results) {
        Map<Integer, Long> ratingCountMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) ratingCountMap.put(i, 0L);

        for (Object[] result : results) {
            Double rating = (Double) result[0];
            Long count = (Long) result[1];
            ratingCountMap.put(rating.intValue(), count);
        }
        return ratingCountMap;
    }

    /**
     * 공통 페이징 변환 메서드
     */
    protected Page<ReviewDTO> toPagedDTO(Page<R> page, int pageNo, CustomUserDetails user) {
        if (page.isEmpty()) {
            Pageable pageable = PageRequest.of(pageNo, 10);
            return Page.empty(pageable);
        }
        return page.map(r -> mapToReviewDTO(r, user));
    }

    protected Page<ReviewDTO> toPagedDTO(Page<R> page, int pageNo) {
        if (page.isEmpty()) {
            Pageable pageable = PageRequest.of(pageNo, 10);
            return Page.empty(pageable);
        }
        return page.map(this::mapToReviewDTO);
    }

    /**
     * 리뷰 TOP 5 조회 공통 매핑 로직
     * - Object[] 결과를 ForTopReviewDTO로 변환
     * - 각 행은 [foodId, reviewId, rating, title, content, memberId(, favoriteCount)] 순서
     */
    protected List<ForTopReviewDTO> mapTopReviewResults(List<Object[]> results) {
        return results.stream()
                .map(row -> {
                    Long memberId = ((Number) row[5]).longValue();
                    String memberName = Objects.requireNonNull(memberService).getNameById(memberId);

                    // 기본 필드 공통 매핑
                    ForTopReviewDTO.ForTopReviewDTOBuilder builder = ForTopReviewDTO.builder()
                            .foodId(((Number) row[0]).longValue())
                            .reviewId(((Number) row[1]).longValue())
                            .rating(((Number) row[2]).doubleValue())
                            .title((String) row[3])
                            .content((String) row[4])
                            .memberName(memberName);

                    if (row.length > 6 && row[6] != null) {
                        builder.favoriteCount(((Number) row[6]).longValue());
                    } else {
                        builder.favoriteCount(null);
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Review 엔티티를 ReviewDTO로 변환 (좋아요 목록용)
     */
    protected ReviewDTO mapFavoriteReviewToDTO(R review, String memberName) {
        if (review == null) return null;

        return ReviewDTO.builder()
                .id((Long) extractId(review))
                .title(review.getTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .memberName(memberName)
                .createdAt(String.valueOf(review.getCreatedAt()))
                .updatedAt(String.valueOf(review.getUpdatedAt()))
                .build();
    }

    /**
     * 멤버별 리뷰 조회 (공통)
     */
    public List<ReviewDTO> findAllByMember(MemberEntity member) {
        if (member == null) throw new IllegalArgumentException("Member cannot be null");

        List<R> reviews;
        reviews = findAllReviewsByMember(member);

        if (reviews.isEmpty()) return Collections.emptyList();

        return reviews.stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * 즐겨찾기 리뷰 조회 (공통 구현)
     */
    public List<ReviewDTO> findAllByMemberFavorited(MemberEntity member) {
        if (member == null) throw new IllegalArgumentException("Member cannot be null");
        List<R> reviews = extractFavoritedReviews(member);
        if (reviews == null || reviews.isEmpty()) return Collections.emptyList();
        return reviews.stream()
                .map(r -> mapFavoriteReviewToDTO(r, member.getName()))
                .collect(Collectors.toList());
    }
}
