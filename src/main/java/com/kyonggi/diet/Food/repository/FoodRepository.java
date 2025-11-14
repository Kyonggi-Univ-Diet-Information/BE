package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.DTO.TopReviewedFoodDTO;
import com.kyonggi.diet.Food.eumer.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FoodRepository {

    private final EntityManager em;

    /**
     * 리뷰 많은 음식 TOP5 (모든 식당 통합)
     */
    public List<TopReviewedFoodDTO> findTop5ReviewedFoods() {
        String sql = """
            SELECT 
                f_id AS id,
                f_type AS restaurant_type,
                f_name AS name,
                f_name_en AS name_en,
                f_price AS price,
                f_cuisine AS cuisine,
                f_food_type AS food_type,
                f_detailed_menu AS detailed_menu,
                f_sub_restaurant AS sub_restaurant,
                review_count
            FROM (
                SELECT 
                    kf.kyongsul_food_id AS f_id,
                    'KYONGSUL' AS f_type,
                    kf.name AS f_name,
                    kf.name_en AS f_name_en,
                    kf.price AS f_price,
                    kf.cuisine AS f_cuisine,
                    kf.food_type AS f_food_type,
                    kf.detailed_menu AS f_detailed_menu,
                    kf.sub_restaurant AS f_sub_restaurant,
                    COUNT(kfr.kyongsul_food_review_id) AS review_count
                FROM kyongsul_food kf
                LEFT JOIN kyongsul_food_review kfr ON kf.kyongsul_food_id = kfr.kyongsul_food_id
                GROUP BY kf.kyongsul_food_id
                
                UNION ALL
                
                SELECT 
                    ef.e_square_food_id AS f_id,
                    'E_SQUARE' AS f_type,
                    ef.name AS f_name,
                    ef.name_en AS f_name_en,
                    ef.price AS f_price,
                    ef.cuisine AS f_cuisine,
                    ef.food_type AS f_food_type,
                    ef.detailed_menu AS f_detailed_menu,
                    NULL AS f_sub_restaurant,
                    COUNT(efr.e_square_food_review_id) AS review_count
                FROM esquare_food ef
                LEFT JOIN esquare_food_review efr ON ef.e_square_food_id = efr.e_square_food_id
                GROUP BY ef.e_square_food_id
                
                UNION ALL
                
                SELECT 
                    sf.sally_box_food_id AS f_id,
                    'SALLY_BOX' AS f_type,
                    sf.name AS f_name,
                    sf.name_en AS f_name_en,
                    sf.price AS f_price,
                    sf.cuisine AS f_cuisine,
                    sf.food_type AS f_food_type,
                    sf.detailed_menu AS f_detailed_menu,
                    NULL AS f_sub_restaurant,
                    COUNT(sfr.sally_box_food_review_id) AS review_count
                FROM sally_box_food sf
                LEFT JOIN sally_box_food_review sfr ON sf.sally_box_food_id = sfr.sally_box_food_id
                GROUP BY sf.sally_box_food_id
            ) AS merged
            ORDER BY review_count DESC
            LIMIT 5
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> result = em.createNativeQuery(sql).getResultList();

        return result.stream().map(row -> TopReviewedFoodDTO.builder()
                .id(((Number) row[0]).longValue())
                .restaurantType(RestaurantType.valueOf((String) row[1]))
                .name((String) row[2])
                .nameEn((String) row[3])
                .price(row[4] != null ? ((Number) row[4]).longValue() : null)
                .cuisine(row[5] != null ? Cuisine.valueOf((String) row[5]) : null)
                .foodType(row[6] != null ? FoodType.valueOf((String) row[6]) : null)
                .detailedMenu(row[7] != null ? DetailedMenu.valueOf((String) row[7]) : null)
                .subRestaurant(row[8] != null ? SubRestaurant.valueOf((String) row[8]) : null)
                .reviewCount(((Number) row[9]).longValue())
                .build())
            .toList();
    }
}
