package com.kyonggi.diet.review.image;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.kyonggi.diet.review.image.dto.PresignedImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewImageUploadService {

    private static final int MAX_IMAGE_COUNT = 3;
    private static final long PRESIGNED_URL_EXPIRATION_MILLIS = 3 * 60 * 1000; // 3분
    private static final String TMP_PREFIX = "tmp/";

    private static final Map<String, String> ALLOWED_CONTENT_TYPE_EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    @Value("${cloud.aws.s3.bucketName.reviewImage}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public List<PresignedImageDTO> issuePresignedUrls(List<String> contentTypes) {
        if (contentTypes == null) {
            contentTypes = List.of();
        }
        if (contentTypes.size() > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException("이미지는 0장 이상 " + MAX_IMAGE_COUNT + "장 이하로 요청해야 합니다.");
        }

        List<PresignedImageDTO> result = new ArrayList<>();
        for (String contentType : contentTypes) {
            String extension = ALLOWED_CONTENT_TYPE_EXTENSIONS.get(contentType);
            if (extension == null) {
                throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다: " + contentType
                        + " (jpeg, png, webp만 가능)");
            }

            String key = TMP_PREFIX + UUID.randomUUID() + "." + extension;
            result.add(PresignedImageDTO.builder()
                    .key(key)
                    .uploadUrl(generatePresignedPutUrl(key, contentType))
                    .build());
        }
        return result;
    }

    private String generatePresignedPutUrl(String key, String contentType) {
        Date expiration = new Date(System.currentTimeMillis() + PRESIGNED_URL_EXPIRATION_MILLIS);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)
                .withContentType(contentType);

        return amazonS3.generatePresignedUrl(request).toString();
    }
}
