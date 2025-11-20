package com.kyonggi.diet.elasticsearch.dto;

import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDocumentDto {

    private Long menuId;
    private RestaurantType restaurantType;
    private SubRestaurant subRestaurant;

    private String menuName;
    private String menuNameEn;
    private FoodType category;

    // calculated fields
    private Long reviewCount;
    private Double averageRating;

    public MenuDocument toDocument() {
        return MenuDocument.createAuto(
                this.restaurantType,
                this.subRestaurant,
                this.menuName,
                this.menuNameEn,
                this.menuId,
                this.reviewCount,
                this.averageRating,
                this.category
        );
    }

    public static MenuDocumentDto fromDocument(MenuDocument doc) {
        return MenuDocumentDto.builder()
                .menuId(doc.getMenuId())
                .restaurantType(doc.getRestaurantType())
                .subRestaurant(doc.getSubRestaurant())
                .menuName(doc.getMenuName())
                .menuNameEn(doc.getMenuNameEn())
                .reviewCount(doc.getReviewCount())
                .category(doc.getCategory())
                .averageRating(doc.getAverageRating())
                .build();
    }
}
