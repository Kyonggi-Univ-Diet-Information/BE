package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.Food.eumer.Cuisine;
import com.kyonggi.diet.Food.eumer.DetailedMenu;
import com.kyonggi.diet.Food.eumer.FoodType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(force = true)
public class ExtendedFood extends Food {

    @Enumerated(EnumType.STRING)
    private Cuisine cuisine;

    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @Enumerated(EnumType.STRING)
    private DetailedMenu detailedMenu;

    public void updateCategory(Cuisine cuisine, FoodType foodType, DetailedMenu detailedMenu) {
        this.cuisine = cuisine;
        this.foodType = foodType;
        this.detailedMenu = detailedMenu;
    }
}
