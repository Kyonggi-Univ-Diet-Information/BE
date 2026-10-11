package com.kyonggi.diet.review.moderation.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

/**
 * 신고 접수 시점에 리뷰 이미지를 reports/ prefix로 COPY해 스냅샷으로 보관한다.
 * 원본 리뷰/이미지가 이후 삭제되어도 신고 처리 시점의 이미지를 그대로 조회할 수 있도록 하기 위함.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Builder
public class ReportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "report_id",
            foreignKey = @ForeignKey(name = "fk_report_image_report")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Report report;

    @Column(nullable = false)
    private String imageKey; // reports/{reportId}/{uuid}.webp 스냅샷 key

    @CreationTimestamp
    private Timestamp createdAt;
}
