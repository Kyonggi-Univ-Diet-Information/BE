package com.kyonggi.diet.review.moderation.report;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
    name = "tbl_report",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_report_reporter_review",
            columnNames = {"reporter_id", "review_id"}
        )
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private MemberEntity reporter;

    // 신고자 이름 스냅샷
    @Column(nullable = false)
    private String reporterNameSnapshot;

    // 신고자 이메일 스냅샷
    @Column(nullable = false)
    private String reporterEmailSnapshot;

    // 신고당한 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private MemberEntity reported;

    // 신고당한 작성자 이름 스냅샷
    @Column(nullable = false)
    private String reportedNameSnapshot;

    // 신고당한 작성자 이메일 스냅샷
    @Column(nullable = false)
    private String reportedEmailSnapshot;

    // 신고 대상 게시물 ID
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    // 신고 대상 게시물의 식당
    @Column
    @Enumerated(EnumType.STRING)
    private RestaurantType restaurantType;

    // 신고 당시 게시물 스냅샷
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reviewContentSnapshot;

    // 리뷰 생성 일자 스냅샷
    private Timestamp reviewCreatedAtSnapshot;

    @Enumerated(EnumType.STRING)
    private ReportReasonType reasonType;

    @Column(length = 500)
    private String etcReason;

    // 신고 접수 시간
    @CreationTimestamp
    private Timestamp reportedAt;
}
