package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.Food.eumer.SallyBoxCategory;
import com.kyonggi.diet.review.domain.SallyBoxFoodReview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@SuperBuilder
public class SallyBoxFood extends Food {

    @Id
    @GeneratedValue
    @Column(name = "sally_box_food_id")
    private Long id;

    private Long price;

    @Enumerated(EnumType.STRING)
    private SallyBoxCategory category;

    @Column(name = "category_kr")
    private String categoryKorean;

    @OneToMany(
            mappedBy = "sallyBoxFood",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SallyBoxFoodReview> sallyBoxFoodReviews;
}
