package com.kyonggi.diet.review.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "조회용 리뷰 이미지 DTO")
public class ReviewImageDTO {

    @Schema(description = "이미지 id (수정 시 유지할 이미지를 고를 때 이 id를 keepImageIds에 담아 보내면 됨)")
    private Long id;

    @Schema(description = "이미지 조회용 presigned GET URL (10분간 유효)")
    private String url;
}
