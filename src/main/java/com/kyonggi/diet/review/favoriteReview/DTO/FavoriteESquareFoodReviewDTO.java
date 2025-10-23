package com.kyonggi.diet.review.favoriteReview.DTO;

import com.kyonggi.diet.member.DTO.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteESquareFoodReviewDTO {

    @Schema(description = "추천 리뷰 ID")
    private Long id;

    @Schema(description = "회원 DTO")
    private MemberDTO memberDTO;

    @Schema(description = "이스퀘어 음식 리뷰 ID")
    private Long eSquareFoodReviewId;

    @Schema(description = "추천 일시")
    private Timestamp createdAt;
}
