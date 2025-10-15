package com.kyonggi.diet.review.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "TOP 5 리뷰들을 위한 DTO")
public class ForTopReviewDTO {
    @Schema(description = "음식 아이디")
    private Long foodId;

    @Schema(description = "리뷰 아이디")
    private Long reviewId;

    @Schema(description = "리뷰 별점")
    private double rating;

    @Schema(description = "리뷰 제목")
    private String title;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "작성자 이름")
    private String memberName;

    @Schema(description = "리뷰 좋아요(추천) 개수 - 인기 TOP5 전용")
    private Long favoriteCount;
}
