package com.kyonggi.diet.review.image.dto;

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
@Schema(description = "이미지 업로드용 presigned URL 발급 요청 DTO")
public class PresignedUrlRequestDTO {

    @Schema(description = "업로드할 이미지들의 Content-Type 목록 (0~3개, 각 image/jpeg, image/png, image/webp 중 하나). 목록 크기가 곧 발급 개수")
    private List<String> contentTypes;
}
