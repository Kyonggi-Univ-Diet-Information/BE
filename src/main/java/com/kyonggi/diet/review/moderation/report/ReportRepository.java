package com.kyonggi.diet.review.moderation.report;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReviewIdAndRestaurantType(
            MemberEntity reporter,
            Long reviewId,
            RestaurantType restaurantType
    );
}
