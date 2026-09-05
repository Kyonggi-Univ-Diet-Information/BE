package com.kyonggi.diet.review.image;

import com.kyonggi.diet.review.image.dto.PresignedImageDTO;
import com.kyonggi.diet.review.image.dto.PresignedUrlRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "리뷰 이미지 업로드 API", description = "리뷰 사진 업로드용 presigned URL 발급")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/images")
public class ReviewImageUploadController {

    private final ReviewImageUploadService reviewImageUploadService;

    @Operation(
            summary = "리뷰 이미지 presigned URL 발급",
            description = "클라이언트가 S3에 직접 업로드할 수 있는 presigned PUT URL을 발급합니다. " +
                    "contentTypes에는 image/jpeg, image/png, image/webp 중 0~3개를 담아 요청합니다. " +
                    "발급된 key는 리뷰 생성/수정 요청 시 함께 전달해야 합니다. URL은 3분 후 만료됩니다."
    )
    @PostMapping("/presigned-url")
    public ResponseEntity<?> issuePresignedUrls(@RequestBody PresignedUrlRequestDTO dto) {
        try {
            List<PresignedImageDTO> result = reviewImageUploadService.issuePresignedUrls(dto.getContentTypes());
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
