package com.kyonggi.diet.member.DTO;

import com.kyonggi.diet.review.DTO.ReviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "마이 페이지 DTO")
public class MyPageDTO {

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "내가 쓴 경슐랭 리뷰")
    private List<ReviewDTO> kyongsulReviews;

    @Schema(description = "내가 쓴 긱사 식당 리뷰")
    private List<ReviewDTO> dietFoodReviews;

    @Schema(description = "좋아요 누른 긱사 식당 리뷰")
    private List<ReviewDTO> favoriteDietFoodReviews;

    @Schema(description = "좋아요 누른 경슐랭 리뷰")
    private List<ReviewDTO> favoriteKyongsulReviews;
}
