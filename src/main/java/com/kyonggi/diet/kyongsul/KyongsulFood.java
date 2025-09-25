package com.kyonggi.diet.kyongsul;

import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KyongsulFood {

    @Id
    @GeneratedValue
    @Column(name = "kyongsul_food_id")
    private Long id;

    private String name;

    @Column(name = "name_en")
    private String nameEn;

    private Long price;

    @Enumerated(EnumType.STRING)
    private SubRestaurant subRestaurant;

    @OneToMany(mappedBy = "kyongsulFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KyongsulFoodReview> kyongsulFoodReviews;

    public void updateNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
