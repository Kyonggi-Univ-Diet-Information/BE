package com.kyonggi.diet.review.favoriteReview.DTO;

import com.kyonggi.diet.member.DTO.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "음식 리뷰 추천 DTO")
public class FavoriteDietFoodReviewDTO {

    @Schema(description = "추천 리뷰 ID")
    private Long id;

    @Schema(description = "회원 DTO")
    private MemberDTO memberDTO;

    @Schema(description = "음식 리뷰 ID")
    private Long dietFoodReviewId;

    @Schema(description = "추천 일시")
    private Timestamp createdAt;
}
