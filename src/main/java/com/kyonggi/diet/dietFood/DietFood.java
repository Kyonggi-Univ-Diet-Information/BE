package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.RestaurantReview;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietFood {

    @Id @GeneratedValue
    @Column(name = "diet_food_id")
    private Long id;
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Enumerated(value = EnumType.STRING)
    private DietFoodType dietFoodType;

    @OneToMany(mappedBy = "dietFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietFoodReview> dietFoodReviews;

    @OneToMany(mappedBy = "dietFood")
    private List<Diet> contents;

    public void updateNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void updateDietFoodType(DietFoodType dietFoodType) {
        this.dietFoodType = dietFoodType;
    }
}
