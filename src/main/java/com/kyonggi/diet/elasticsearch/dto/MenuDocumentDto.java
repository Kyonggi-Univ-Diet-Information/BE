package com.kyonggi.diet.elasticsearch.dto;

import com.kyonggi.diet.Food.eumer.RestaurantType;
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

    private Long menuId;                 // RDB의 menu PK
    private RestaurantType restaurantType;
    private String menuName;

    public MenuDocument toDocument() {
        String esId = this.restaurantType.name() + "_" + this.menuId;

        return new MenuDocument(
                esId,
                this.restaurantType,
                this.menuName,
                this.menuId
        );
    }

    public static MenuDocumentDto fromDocument(MenuDocument doc) {
        return MenuDocumentDto.builder()
                .menuId(doc.getMenuId())
                .restaurantType(doc.getRestaurantType())
                .menuName(doc.getMenuName())
                .build();
    }

}
