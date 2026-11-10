package com.kyonggi.diet.review;

import com.kyonggi.diet.review.keyword.KeywordLevel;
import com.kyonggi.diet.review.DTO.CreateReviewDTO;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewKeywordTest {

    private final DietFoodReviewService service = new DietFoodReviewService(
            null, null, null, null, null, null, null);

    @Test
    void 키워드를_하나도_선택하지_않고_작성하면_예외() {
        ReviewDTO dto = ReviewDTO.builder().title("t").content("c").rating(4).build();

        assertThatThrownBy(() -> service.createReview(dto, 1L, "a@a.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("키워드");
    }

    @Test
    void 키워드를_하나도_선택하지_않고_수정하면_예외() {
        CreateReviewDTO dto = CreateReviewDTO.builder().title("t").content("c").rating(4).build();

        assertThatThrownBy(() -> service.modifyReview(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("키워드");
    }

    @Test
    void 키워드_교체와_해제() {
        DietFoodReview review = DietFoodReview.builder()
                .saltLevel(KeywordLevel.HIGH)
                .spicyLevel(KeywordLevel.LOW)
                .build();

        // 같은 카테고리의 다른 키워드로 교체, 맵기는 해제, 양 새로 선택
        review.updateKeywords(KeywordLevel.LOW, null, KeywordLevel.HIGH, null);

        assertThat(review.getSaltLevel()).isEqualTo(KeywordLevel.LOW);
        assertThat(review.getSpicyLevel()).isNull();
        assertThat(review.getPortionLevel()).isEqualTo(KeywordLevel.HIGH);
        assertThat(review.getValueLevel()).isNull();
    }

    @Test
    void 키워드가_없는_기존_리뷰는_null로_조회된다() {
        DietFoodReview legacy = DietFoodReview.builder().title("old").build();

        assertThat(legacy.getSaltLevel()).isNull();
        assertThat(legacy.getValueLevel()).isNull();
    }
}
