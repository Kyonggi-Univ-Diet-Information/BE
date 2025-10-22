package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.Food.eumer.DietFoodType;
import com.kyonggi.diet.review.domain.DietFoodReview;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(force = true)
public class DietFood extends Food {

    @Id @GeneratedValue
    @Column(name = "diet_food_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DietFoodType dietFoodType;

    @OneToMany(mappedBy = "dietFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietFoodReview> dietFoodReviews;

    @OneToMany(mappedBy = "dietFood")
    private List<Diet> contents;

    public void updateDietFoodType(DietFoodType dietFoodType) {
        this.dietFoodType = dietFoodType;
    }
}
