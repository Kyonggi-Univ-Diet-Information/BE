package com.kyonggi.diet.dietFood;

import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.review.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(value = EnumType.STRING)
    private DietFoodType dietFoodType;

    @OneToMany(mappedBy = "dietFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "dietFood")
    private List<Diet> contents;

}
