package com.kyonggi.diet.review.moderation.report;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.review.domain.Review;
import com.kyonggi.diet.review.image.ReviewImageCommitService;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonDto;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonEtcDto;
import com.kyonggi.diet.review.moderation.report.dto.ReportReasonTypeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final MemberRepository memberRepository;
    private final ReviewImageCommitService reviewImageCommitService;

    @Transactional
    public void report(CustomUserDetails reporter, RestaurantType type,
                       Review review, Long reviewId, ReportReasonDto dto, List<String> reviewImageKeys) {

        if (dto.getType() == ReportReasonType.ETC &&
                (dto.getEtcReason() == null || dto.getEtcReason().isBlank())) {
            throw new IllegalArgumentException("기타 신고 사유는 내용을 입력해야 합니다.");
        }

        MemberEntity reporterMember = findMemberById(reporter.getMemberId());
        MemberEntity reportedMember = review.getMember();

        Report report = Report.builder()
                .reporter(reporterMember)
                .reporterNameSnapshot(reporterMember.getName())
                .reporterEmailSnapshot(reporterMember.getEmail())
                .reported(reportedMember)
                .reportedNameSnapshot(reportedMember.getName())
                .reportedEmailSnapshot(reportedMember.getEmail())
                .reviewId(reviewId)
                .restaurantType(type)
                .reviewContentSnapshot(review.getContent())
                .reviewCreatedAtSnapshot(review.getCreatedAt())
                .reasonType(dto.getType())
                .etcReason(dto.getEtcReason())
                .build();
        reportRepository.save(report);

        saveImageSnapshots(report, reviewImageKeys);
    }

    /** 신고 시점의 리뷰 이미지를 reports/ 로 별도 COPY해 스냅샷으로 남긴다 (원본 리뷰가 이후 수정/삭제돼도 유지되도록) */
    private void saveImageSnapshots(Report report, List<String> reviewImageKeys) {
        List<String> snapshotKeys = reviewImageCommitService.copyToReportSnapshot(report.getId(), reviewImageKeys);
        if (snapshotKeys.isEmpty()) return;

        List<ReportImage> reportImages = new ArrayList<>();
        for (String snapshotKey : snapshotKeys) {
            reportImages.add(ReportImage.builder()
                    .report(report)
                    .imageKey(snapshotKey)
                    .build());
        }
        reportImageRepository.saveAll(reportImages);
    }

    public List<ReportReasonTypeResponse> getAllReportReasonTypes() {
        return Arrays.stream(ReportReasonType.values())
                .map(type -> new ReportReasonTypeResponse(
                        type.name(),
                        type.getDescription()
                ))
                .toList();
    }

    public boolean existReportForReview(CustomUserDetails me, Long reviewId, RestaurantType type) {
        MemberEntity member = findMemberById(me.getMemberId());
        return reportRepository.existsByReporterAndReviewIdAndRestaurantType(member, reviewId, type);
    }

    private MemberEntity findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
