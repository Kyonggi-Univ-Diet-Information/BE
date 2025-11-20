package com.kyonggi.diet.elasticsearch.document;

import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.elasticsearch.dto.MenuDocumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "menu_index")
@Builder
@Getter
public class MenuDocument {

    @Id
    private String id;   // ES에서 유니크 ID ← "diet_12" 같은 문자열

    private RestaurantType restaurantType;
    private String menuName;
    private Long menuId; // 실제 음식 id

    public MenuDocument(String id, RestaurantType restaurantType, String menuName, Long menuId) {
        this.id = id;
        this.restaurantType = restaurantType;
        this.menuName = menuName;
        this.menuId = menuId;
    }

    public MenuDocument() {}

}

