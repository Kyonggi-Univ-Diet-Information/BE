package com.kyonggi.diet.review.DTO;

import com.kyonggi.diet.review.image.dto.ReviewImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 DTO")
public class ReviewDTO {

    @Schema(description = "리뷰 아이디")
    private Long id;

    @Schema(description = "리뷰 별점")
    private double rating;

    @Schema(description = "리뷰 제목")
    private String title;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "작성자 이름")
    private String memberName;

    @Schema(description = "본인 작성 여부")
    private boolean isMyReview;

    @Schema(description = "생성 일자")
    private String createdAt;

    @Schema(description = "수정 일자")
    private String updatedAt;

    @Schema(description = "새로 추가할 이미지 tmp key 목록 (생성 요청 시에만 사용)")
    private List<String> imageKeys;

    @Schema(description = "등록된 리뷰 이미지 목록 (조회 응답에서만 채워짐)")
    private List<ReviewImageDTO> images;
}
