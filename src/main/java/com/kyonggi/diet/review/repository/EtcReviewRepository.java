package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.review.DTO.RecentReviewDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EtcReviewRepository {

    private final EntityManager em;

    private static final String BASE_SQL = """
        SELECT
            f_id AS foodId,
            r_id AS reviewId,
            f_type AS restaurantType,
            member_name AS memberName,
            rating,
            title,
            content,
            created_at AS createdAt
        FROM (
            SELECT
                kfr.kyongsul_food_id AS f_id,
                kfr.kyongsul_food_review_id AS r_id,
                'KYONGSUL' AS f_type,
                m.name AS member_name,
                kfr.rating,
                kfr.title,
                kfr.content,
                kfr.created_at,
                kfr.member_id AS member_id
            FROM kyongsul_food_review kfr
            JOIN tbl_members m ON m.member_id = kfr.member_id

            UNION ALL

            SELECT
                efr.e_square_food_id AS f_id,
                efr.e_square_food_review_id AS r_id,
                'E_SQUARE' AS f_type,
                m.name AS member_name,
                efr.rating,
                efr.title,
                efr.content,
                efr.created_at,
                efr.member_id AS member_id
            FROM esquare_food_review efr
            JOIN tbl_members m ON m.member_id = efr.member_id

            UNION ALL

            SELECT
                sfr.sally_box_food_id AS f_id,
                sfr.sally_box_food_review_id AS r_id,
                'SALLY_BOX' AS f_type,
                m.name AS member_name,
                sfr.rating,
                sfr.title,
                sfr.content,
                sfr.created_at,
                sfr.member_id AS member_id
            FROM sally_box_food_review sfr
            JOIN tbl_members m ON m.member_id = sfr.member_id
        ) AS merged
        %s
        ORDER BY created_at DESC
        LIMIT 5
    """;

    public List<RecentReviewDTO> findRecent5Reviews() {
        return execute(BASE_SQL.formatted(""));
    }

    public List<RecentReviewDTO> findRecent5ReviewsExcludeBlocked(
            List<Long> blockedMemberIds) {

        if (blockedMemberIds == null || blockedMemberIds.isEmpty()) {
            return findRecent5Reviews();
        }

        return execute(
                BASE_SQL.formatted("WHERE member_id NOT IN :blockedIds"),
                blockedMemberIds
        );
    }

    /* ================= 공통 메서드 ================= */

    private List<RecentReviewDTO> execute(String sql) {
        List<Object[]> results =
                em.createNativeQuery(sql).getResultList();
        return map(results);
    }

    private List<RecentReviewDTO> execute(
            String sql, List<Long> blockedIds) {

        List<Object[]> results =
                em.createNativeQuery(sql)
                        .setParameter("blockedIds", blockedIds)
                        .getResultList();
        return map(results);
    }

    private List<RecentReviewDTO> map(List<Object[]> results) {
        return results.stream()
                .map(row -> new RecentReviewDTO(
                        ((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue(),
                        RestaurantType.valueOf((String) row[2]),
                        (String) row[3],
                        ((Number) row[4]).doubleValue(),
                        (String) row[5],
                        (String) row[6],
                        row[7].toString()
                ))
                .toList();
    }
}
