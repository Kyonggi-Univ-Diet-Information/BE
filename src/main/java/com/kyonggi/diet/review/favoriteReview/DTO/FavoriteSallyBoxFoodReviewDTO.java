package com.kyonggi.diet.review.favoriteReview.DTO;

import com.kyonggi.diet.member.DTO.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteSallyBoxFoodReviewDTO {
    @Schema(description = "추천 리뷰 ID")
    private Long id;

    @Schema(description = "회원 DTO")
    private MemberDTO memberDTO;

    @Schema(description = "샐리박스 음식 리뷰 ID")
    private Long sallyBoxFoodReviewId;

    @Schema(description = "추천 일시")
    private Timestamp createdAt;
}
