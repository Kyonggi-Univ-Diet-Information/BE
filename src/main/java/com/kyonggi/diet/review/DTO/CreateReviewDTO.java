package com.kyonggi.diet.review.DTO;

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
@Schema(description = "리뷰 생성 DTO")
public class CreateReviewDTO {

    @Schema(description = "리뷰 별점")
    private double rating;

    @Schema(description = "리뷰 제목")
    private String title;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "새로 추가할 이미지 tmp key 목록 (presigned URL로 업로드한 tmp/{uuid}.webp)")
    private List<String> imageKeys;

    @Schema(description = "수정 시 유지할 기존 이미지 id 목록. 여기 없는 기존 이미지는 삭제됨 (생성 시에는 사용 안 함)")
    private List<Long> keepImageIds;
}
