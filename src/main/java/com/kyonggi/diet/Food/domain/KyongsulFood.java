package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.Food.eumer.KyongsulCategory;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(force = true)
public class KyongsulFood extends ExtendedFood {

    @Id
    @GeneratedValue
    @Column(name = "kyongsul_food_id")
    private Long id;

    private Long price;

    @Enumerated(EnumType.STRING)
    private SubRestaurant subRestaurant;

    @Enumerated(EnumType.STRING)
    private KyongsulCategory category;

    @Column(name = "category_kr")
    private String categoryKorean;

    @OneToMany(
            mappedBy = "baseFood",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<KyongsulSetFood> foodSets;

    @OneToMany(
            mappedBy = "kyongsulFood",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<KyongsulFoodReview> kyongsulFoodReviews;
}
