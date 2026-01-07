package com.kyonggi.diet.search;

import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.search.DTO.SearchRequestDTO;
import com.kyonggi.diet.search.DTO.SearchResponseDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SearchRepository {

    private final EntityManager em;

    public List<SearchResponseDTO> search(SearchRequestDTO dto) {

        StringBuilder sql = new StringBuilder("""
            SELECT *
            FROM (
                SELECT 
                    kf.kyongsul_food_id AS id,
                    'KYONGSUL' AS restaurant_type,
                    kf.name AS name,
                    kf.name_en AS name_en,
                    kf.cuisine AS cuisine,
                    kf.food_type AS food_type,
                    kf.detailed_menu AS detailed_menu,
                    kf.sub_restaurant AS sub_restaurant,
                    kf.price AS price,
                    COUNT(kfr.kyongsul_food_review_id) AS review_count,
                    ROUND(COALESCE(AVG(kfr.rating), 0)::numeric, 1) AS average_rating
                FROM kyongsul_food kf
                LEFT JOIN kyongsul_food_review kfr 
                    ON kf.kyongsul_food_id = kfr.kyongsul_food_id
                GROUP BY kf.kyongsul_food_id

                UNION ALL

                SELECT 
                    ef.e_square_food_id AS id,
                    'E_SQUARE' AS restaurant_type,
                    ef.name AS name,
                    ef.name_en AS name_en,
                    ef.cuisine AS cuisine,
                    ef.food_type AS food_type,
                    ef.detailed_menu AS detailed_menu,
                    NULL AS sub_restaurant,
                    ef.price AS price,
                    COUNT(efr.e_square_food_review_id) AS review_count,
                    ROUND(COALESCE(AVG(efr.rating), 0)::numeric, 1) AS average_rating
                FROM esquare_food ef
                LEFT JOIN esquare_food_review efr 
                    ON ef.e_square_food_id = efr.e_square_food_id
                GROUP BY ef.e_square_food_id

                UNION ALL

                SELECT 
                    sf.sally_box_food_id AS id,
                    'SALLY_BOX' AS restaurant_type,
                    sf.name AS name,
                    sf.name_en AS name_en,
                    sf.cuisine AS cuisine,
                    sf.food_type AS food_type,
                    sf.detailed_menu AS detailed_menu,
                    NULL AS sub_restaurant,
                    sf.price AS price,
                    COUNT(sfr.sally_box_food_review_id) AS review_count,
                    ROUND(COALESCE(AVG(sfr.rating), 0)::numeric, 1) AS average_rating
                FROM sally_box_food sf
                LEFT JOIN sally_box_food_review sfr 
                    ON sf.sally_box_food_id = sfr.sally_box_food_id
                GROUP BY sf.sally_box_food_id
            ) merged
            WHERE 1 = 1
        """);

        Map<String, Object> params = new HashMap<>();

        // 키워드 검색
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            sql.append("""
                AND (LOWER(name) LIKE LOWER(:keyword)
                 OR LOWER(name_en) LIKE LOWER(:keyword))
            """);
            params.put("keyword", "%" + dto.getKeyword() + "%");
        }

        // foodType 필터링
        if (dto.getFoodType() != null) {
            sql.append(" AND food_type = :foodType");
            params.put("foodType", dto.getFoodType().name());
        }

        // 식당 필터링
        if (dto.getRestaurantType() != null
                && dto.getRestaurantType() != SearchRestaurantType.ALL) {
            sql.append(" AND restaurant_type = :restaurantType");
            params.put("restaurantType", dto.getRestaurantType().name());
        }

        // 최소, 최대 가격 필터링
        if (dto.getPriceMin() != null) {
            sql.append(" AND price >= :priceMin");
            params.put("priceMin", dto.getPriceMin());
        }
        if (dto.getPriceMax() != null) {
            sql.append(" AND price <= :priceMax");
            params.put("priceMax", dto.getPriceMax());
        }

        SortingType sort = dto.getSortingType();

        if (sort != null) {
            switch (sort) {
                case BASIC -> {
                    // 정렬 없음(기본값)
                }
                case REVIEW_COUNT_DESC -> sql.append(" ORDER BY review_count DESC");
                case RATING_DESC -> sql.append(" ORDER BY average_rating DESC");
                case PRICE_ASC -> sql.append(" ORDER BY price ASC");
                case PRICE_DESC -> sql.append(" ORDER BY price DESC");
            }
        }


        var query = em.createNativeQuery(sql.toString());

        params.forEach(query::setParameter);

        List<Object[]> result = query.getResultList();

        return result.stream().map(row -> SearchResponseDTO.builder()
                        .menuId(((Number) row[0]).longValue())
                        .restaurantType(RestaurantType.valueOf((String) row[1]))
                        .name((String) row[2])
                        .nameEn((String) row[3])
                        .cuisine(row[4] != null ? Cuisine.valueOf((String) row[4]) : null)
                        .foodType(row[5] != null ? FoodType.valueOf((String) row[5]) : null)
                        .detailedMenu(row[6] != null ? DetailedMenu.valueOf((String) row[6]) : null)
                        .subRestaurant(row[7] != null ? SubRestaurant.valueOf((String) row[7]) : null)
                        .price(((Number) row[8]).longValue())
                        .reviewCount(((Number) row[9]).longValue())
                        .averageRating(row[10] != null ? ((Number) row[10]).doubleValue() : 0.0)
                        .build())
                .toList();
    }
}
