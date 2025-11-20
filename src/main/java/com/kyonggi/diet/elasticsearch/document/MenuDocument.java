package com.kyonggi.diet.elasticsearch.document;

import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "menu_index")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDocument {

    @Id
    private String id;

    private RestaurantType restaurantType;
    private SubRestaurant subRestaurant;
    private String menuName;
    private String menuNameEn;
    private Long menuId;
    private FoodType category;

    private Long reviewCount;
    private Double averageRating;

    private String searchText;

    public static MenuDocument createAuto(
            RestaurantType restaurantType,
            SubRestaurant subRestaurant,
            String menuName,
            String menuNameEn,
            Long menuId,
            Long reviewCount,
            Double averageRating,
            FoodType category
    ) {

        String id = restaurantType.toString() + "_" + menuId;

        String search = buildSearchText(menuName, menuNameEn, category.getKoreanName());

        return MenuDocument.builder()
                .id(id)
                .restaurantType(restaurantType)
                .subRestaurant(subRestaurant)
                .menuName(menuName)
                .menuNameEn(menuNameEn)
                .menuId(menuId)
                .reviewCount(reviewCount)
                .category(category)
                .averageRating(averageRating)
                .searchText(search)
                .build();
    }

    private static String buildSearchText(String name, String en, String category) {
        StringBuilder sb = new StringBuilder();
        if (name != null) sb.append(name).append(" ");
        if (en != null) sb.append(en).append(" ");
        if (category != null) sb.append(category);
        return sb.toString().trim();
    }
}
