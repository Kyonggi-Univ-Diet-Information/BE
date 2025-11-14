package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.review.DTO.RecentReviewDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final EntityManager em;

    public List<RecentReviewDTO> findRecent5Reviews() {
        String sql = """
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
                    kfr.created_at
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
                    efr.created_at
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
                    sfr.created_at
                FROM sally_box_food_review sfr
                JOIN tbl_members m ON m.member_id = sfr.member_id
            ) AS merged
            ORDER BY created_at DESC
            LIMIT 5
        """;

        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        return results.stream()
                .map(row -> new RecentReviewDTO(
                        ((Number) row[0]).longValue(),             // foodId
                        ((Number) row[1]).longValue(),             // reviewId
                        RestaurantType.valueOf((String) row[2]),   // restaurantType
                        (String) row[3],                           // memberName
                        ((Number) row[4]).doubleValue(),           // rating
                        (String) row[5],                           // title
                        (String) row[6],                           // content
                        row[7].toString()                          // createdAt
                ))
                .toList();
    }
}
