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
@Schema(description = "발급된 presigned URL 정보")
public class PresignedImageDTO {

    @Schema(description = "업로드 후 리뷰 생성 시 서버에 전달할 임시 이미지 key (tmp/{uuid}.webp)")
    private String key;

    @Schema(description = "클라이언트가 이 URL로 PUT 요청하면 S3에 직접 업로드됨. 3분 후 만료")
    private String uploadUrl;
}
