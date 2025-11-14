package com.kyonggi.diet.member;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.review.DTO.MyReviewDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {

    private final EntityManager em;

    // ✅ 내가 쓴 리뷰
    public Page<MyReviewDTO> findMyWrittenReviews(Long memberId, Pageable pageable) {
        String sql = """
                SELECT
                    r_id AS reviewId,
                    f_id AS foodId,
                    f_type AS restaurantType,
                    rating,
                    title,
                    content,
                    member_name AS memberName,
                    created_at AS createdAt
                FROM (
                    SELECT
                        kfr.kyongsul_food_review_id AS r_id,
                        kfr.kyongsul_food_id AS f_id,
                        'KYONGSUL' AS f_type,
                        kfr.rating,
                        kfr.title,
                        kfr.content,
                        m.name AS member_name,
                        kfr.created_at
                    FROM kyongsul_food_review kfr
                    JOIN tbl_members m ON m.member_id = kfr.member_id
                    WHERE kfr.member_id = ?1

                    UNION ALL

                    SELECT
                        efr.e_square_food_review_id AS r_id,
                        efr.e_square_food_id AS f_id,
                        'E_SQUARE' AS f_type,
                        efr.rating,
                        efr.title,
                        efr.content,
                        m.name AS member_name,
                        efr.created_at
                    FROM esquare_food_review efr
                    JOIN tbl_members m ON m.member_id = efr.member_id
                    WHERE efr.member_id = ?1

                    UNION ALL

                    SELECT
                        sfr.sally_box_food_review_id AS r_id,
                        sfr.sally_box_food_id AS f_id,
                        'SALLY_BOX' AS f_type,
                        sfr.rating,
                        sfr.title,
                        sfr.content,
                        m.name AS member_name,
                        sfr.created_at
                    FROM sally_box_food_review sfr
                    JOIN tbl_members m ON m.member_id = sfr.member_id
                    WHERE sfr.member_id = ?1
                ) merged
                ORDER BY created_at DESC
                """;

        Query query = em.createNativeQuery(sql)
                .setParameter(1, memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Object[]> results = query.getResultList();

        List<MyReviewDTO> content = results.stream()
                .map(row -> MyReviewDTO.builder()
                        .reviewId(((Number) row[0]).longValue())
                        .foodId(((Number) row[1]).longValue())
                        .restaurantType(RestaurantType.valueOf((String) row[2]))
                        .rating(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0)
                        .title((String) row[4])
                        .content((String) row[5])
                        .memberName((String) row[6])
                        .createdAt(row[7] != null ? row[7].toString() : null)
                        .build())
                .toList();

        long total = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM (
                        SELECT 1 FROM kyongsul_food_review WHERE member_id = ?1
                        UNION ALL
                        SELECT 1 FROM esquare_food_review WHERE member_id = ?1
                        UNION ALL
                        SELECT 1 FROM sally_box_food_review WHERE member_id = ?1
                    ) total
                """).setParameter(1, memberId).getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, total);
    }

    // ✅ 내가 좋아요한 리뷰
    public Page<MyReviewDTO> findMyFavoritedReviews(Long memberId, Pageable pageable) {
        String sql = """
                SELECT
                    r_id AS reviewId,
                    f_id AS foodId,
                    f_type AS restaurantType,
                    rating,
                    title,
                    content,
                    member_name AS memberName,
                    created_at AS createdAt
                FROM (
                    SELECT
                        r.kyongsul_food_review_id AS r_id,
                        r.kyongsul_food_id AS f_id,
                        'KYONGSUL' AS f_type,
                        r.rating,
                        r.title,
                        r.content,
                        m.name AS member_name,
                        r.created_at
                    FROM favorite_kyongsul_food_review fr
                    JOIN kyongsul_food_review r ON fr.kyongsul_food_review_id = r.kyongsul_food_review_id
                    JOIN tbl_members m ON r.member_id = m.member_id
                    WHERE fr.member_id = ?1

                    UNION ALL

                    SELECT
                        r.e_square_food_review_id AS r_id,
                        r.e_square_food_id AS f_id,
                        'E_SQUARE' AS f_type,
                        r.rating,
                        r.title,
                        r.content,
                        m.name AS member_name,
                        r.created_at
                    FROM favoriteesquare_food_review fr
                    JOIN esquare_food_review r ON fr.e_square_food_review_id = r.e_square_food_review_id
                    JOIN tbl_members m ON r.member_id = m.member_id
                    WHERE fr.member_id = ?1

                    UNION ALL

                    SELECT
                        r.sally_box_food_review_id AS r_id,
                        r.sally_box_food_id AS f_id,
                        'SALLY_BOX' AS f_type,
                        r.rating,
                        r.title,
                        r.content,
                        m.name AS member_name,
                        r.created_at
                    FROM favorite_sally_box_food_review fr
                    JOIN sally_box_food_review r ON fr.sally_box_food_review_id = r.sally_box_food_review_id
                    JOIN tbl_members m ON r.member_id = m.member_id
                    WHERE fr.member_id = ?1
                ) merged
                ORDER BY created_at DESC
                """;

        Query query = em.createNativeQuery(sql)
                .setParameter(1, memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Object[]> results = query.getResultList();

        List<MyReviewDTO> content = results.stream()
                .map(row -> MyReviewDTO.builder()
                        .reviewId(((Number) row[0]).longValue())
                        .foodId(((Number) row[1]).longValue())
                        .restaurantType(RestaurantType.valueOf((String) row[2]))
                        .rating(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0)
                        .title((String) row[4])
                        .content((String) row[5])
                        .memberName((String) row[6])
                        .createdAt(row[7] != null ? row[7].toString() : null)
                        .build())
                .toList();

        long total = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM (
                        SELECT 1 FROM favorite_kyongsul_food_review WHERE member_id = ?1
                        UNION ALL
                        SELECT 1 FROM favoriteesquare_food_review WHERE member_id = ?1
                        UNION ALL
                        SELECT 1 FROM favorite_sally_box_food_review WHERE member_id = ?1
                    ) total
                """).setParameter(1, memberId).getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, total);
    }
}
