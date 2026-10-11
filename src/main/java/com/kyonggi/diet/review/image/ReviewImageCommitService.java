package com.kyonggi.diet.review.image;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * presigned URL로 tmp/ 에 업로드된 이미지를 리뷰에 실제로 반영(reviews/ 로 이동)하거나,
 * 더 이상 필요 없어진 리뷰 이미지를 S3에서 정리하는 역할.
 */
@Service
@RequiredArgsConstructor
public class ReviewImageCommitService {

    private static final String TMP_PREFIX = "tmp/";
    private static final String REVIEWS_PREFIX = "reviews/";
    private static final long VIEW_URL_EXPIRATION_MILLIS = 10 * 60 * 1000; // 10분

    @Value("${cloud.aws.s3.bucketName.reviewImage}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    /**
     * tmp key 목록을 검증(존재 여부) 후 reviews/ 로 COPY하고 tmp 원본은 삭제한다.
     * 반환 리스트의 순서는 입력 순서와 동일하며, 그대로 sortOrder로 사용하면 된다.
     */
    public List<String> commitTmpImages(List<String> tmpKeys) {
        if (tmpKeys == null || tmpKeys.isEmpty()) {
            return List.of();
        }

        List<String> committedKeys = new ArrayList<>();
        for (String tmpKey : tmpKeys) {
            if (tmpKey == null || !tmpKey.startsWith(TMP_PREFIX)) {
                throw new IllegalArgumentException("잘못된 이미지 key 입니다: " + tmpKey);
            }
            if (!amazonS3.doesObjectExist(bucketName, tmpKey)) {
                throw new NoSuchElementException("업로드되지 않았거나 만료된 이미지입니다: " + tmpKey);
            }

            String newKey = REVIEWS_PREFIX + tmpKey.substring(TMP_PREFIX.length());
            amazonS3.copyObject(bucketName, tmpKey, bucketName, newKey);
            amazonS3.deleteObject(bucketName, tmpKey);
            committedKeys.add(newKey);
        }
        return committedKeys;
    }

    /** reviews/ 에 있는 이미지 객체들을 S3에서 삭제한다. */
    public void deleteReviewImages(List<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return;
        }
        for (String key : imageKeys) {
            amazonS3.deleteObject(bucketName, key);
        }
    }

    /** 비공개 버킷에 저장된 이미지를 조회할 수 있는 presigned GET URL을 발급한다. */
    public String generateViewUrl(String imageKey) {
        Date expiration = new Date(System.currentTimeMillis() + VIEW_URL_EXPIRATION_MILLIS);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, imageKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(request).toString();
    }

    /**
     * 신고 접수 시점의 리뷰 이미지를 reports/{reportId}/ 로 별도 COPY해 스냅샷을 남긴다.
     * 원본(reviews/)은 건드리지 않는다 (리뷰가 나중에 수정/삭제돼도 신고 스냅샷은 유지되어야 하므로).
     */
    public List<String> copyToReportSnapshot(Long reportId, List<String> reviewImageKeys) {
        if (reviewImageKeys == null || reviewImageKeys.isEmpty()) {
            return List.of();
        }

        List<String> snapshotKeys = new ArrayList<>();
        for (String reviewImageKey : reviewImageKeys) {
            String fileName = reviewImageKey.substring(reviewImageKey.lastIndexOf('/') + 1);
            String snapshotKey = "reports/" + reportId + "/" + fileName;
            amazonS3.copyObject(bucketName, reviewImageKey, bucketName, snapshotKey);
            snapshotKeys.add(snapshotKey);
        }
        return snapshotKeys;
    }
}
