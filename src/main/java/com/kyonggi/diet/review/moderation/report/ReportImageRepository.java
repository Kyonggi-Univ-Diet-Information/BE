package com.kyonggi.diet.review.moderation.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {
    List<ReportImage> findAllByReport_Id(Long reportId);
}
