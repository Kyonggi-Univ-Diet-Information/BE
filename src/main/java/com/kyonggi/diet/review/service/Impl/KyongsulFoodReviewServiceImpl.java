package com.kyonggi.diet.review.service.Impl;

import com.kyonggi.diet.kyongsul.KyongsulFood;
import com.kyonggi.diet.kyongsul.KyongsulFoodRepository;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import com.kyonggi.diet.review.repository.KyongsulFoodReviewRepository;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly=true)
@Slf4j
@RequiredArgsConstructor
public class KyongsulFoodReviewServiceImpl implements KyongsulFoodReviewService {

    private final KyongsulFoodReviewRepository kyongsulFoodReviewRepository;
    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    /**
     * 경슐랭 리뷰 생성 메서드
     * @param reviewDTO (ReviewDTO)
     * @param foodId (Long)
     * @param email (String)
     */
    @Override
    @Transactional
    public void createKyongsulFoodReview(ReviewDTO reviewDTO, Long foodId, String email) {
        KyongsulFood food = kyongsulFoodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("No found KyongsulFood"));

        MemberEntity member = memberService.getMemberByEmail(email);

        KyongsulFoodReview review = KyongsulFoodReview.builder()
                .title(reviewDTO.getTitle())
                .rating(reviewDTO.getRating())
                .content(reviewDTO.getContent())
                .kyongsulFood(food)
                .member(member).build();

        kyongsulFoodReviewRepository.save(review);

        food.getKyongsulFoodReviews().add(review);
        kyongsulFoodRepository.save(food);
    }

    /**
     * 경슐랭 음식 리뷰 삭제 메서드
     * @param reviewId (Long)
     */
    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        kyongsulFoodReviewRepository.delete(findOne(reviewId));
    }

    /**
     * 경슐랭 음식 리뷰 수정 메서드
     * @param reviewId (Long)
     * @param createReviewDTO (CreateReviewDTO)
     */
    @Override
    @Transactional
    public void modifyKyongsulFoodReview(Long reviewId, CreateReviewDTO createReviewDTO) {
        KyongsulFoodReview review = findOne(reviewId);
        review.updateReview(createReviewDTO.getRating(), createReviewDTO.getTitle(), createReviewDTO.getContent());
        kyongsulFoodReviewRepository.save(review);
    }

    /**
     * 경슐랭 음식 리뷰 DTO 한개 조회 메서드
     * @param reviewId (Long)
     * @return reviewDTO
     */
    @Override
    public ReviewDTO findReviewDTOById(Long reviewId) {
        KyongsulFoodReview review = findOne(reviewId);
        ReviewDTO reviewDTO = mapToReviewDTO(review);
        reviewDTO.setMemberName(review.getMember().getName());
        return reviewDTO;
    }

    /**
     * 페이징된 ReviewDTO 조회
     * @param pageNo (Int)
     * @return Page<ReviewDTO>
     */
    @Override
    public Page<ReviewDTO> getAllReviewsByFoodIdPaged(Long foodId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<KyongsulFoodReview> all = kyongsulFoodReviewRepository.findAllByKyongsulFoodId(foodId, pageable);
        if (all.isEmpty()) {
            throw new EntityNotFoundException("Can't find Paged Kyongsul Food reviews");
        }
        return  all.map(this::mapToReviewDTO);
    }

    /**
     * 경슐랭 음식에 대한 평균 평점 구하기 메서드
     * @param foodId (Long)
     * @return averageRating (Double)
     */
    @Override
    public Double getAverageRatingForReview(Long foodId) {
        try {
            return kyongsulFoodReviewRepository.findAverageRatingByKyongsulFoodId(foodId);
        } catch (NullPointerException e) {
            return 0.0;
        }
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
        KyongsulFoodReview review = findOne(reviewId);
        return member.getId().equals(review.getMember().getId());
    }

    /**
     * 해당 음식에 대한 각 리뷰 rating 카운팅 개수 리턴  메서드
     * @param foodId (Long)
     * @return Map<Integer, Long>
     */
    @Override
    public Map<Integer, Long> getCountEachRating(Long foodId) {
        List<Object[]> results = kyongsulFoodReviewRepository.findRatingCountByKyongsulFoodId(foodId);

        Map<Integer, Long> ratingCountMap = new HashMap<>();
        for  (int i = 1; i <= 5; i++) {
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
     * 경슐랭 음식 엔티티 조회
     * @param reviewId (Long)
     * @return kyongsulFoodReview
     */
    private KyongsulFoodReview findOne(Long reviewId) {
        return kyongsulFoodReviewRepository.findById(reviewId)
                .orElseThrow(() -> new  NoSuchElementException("No found KyongsulFoodReview"));
    }

    /**
     * Review -> ReviewDTO
     * @param kyongsulFoodReview (KyongsulFoodReview)
     * @return ReviewDTO (ReviewDTO)
     */
    private ReviewDTO mapToReviewDTO(KyongsulFoodReview kyongsulFoodReview) {
        ReviewDTO dto = modelMapper.map(kyongsulFoodReview, ReviewDTO.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        if (kyongsulFoodReview.getCreatedAt() != null) {
            dto.setCreatedAt(kyongsulFoodReview.getCreatedAt().toLocalDateTime().format(formatter));
        }
        if (kyongsulFoodReview.getUpdatedAt() != null) {
            dto.setUpdatedAt(kyongsulFoodReview.getUpdatedAt().toLocalDateTime().format(formatter));
        }

        return dto;
    }
}
