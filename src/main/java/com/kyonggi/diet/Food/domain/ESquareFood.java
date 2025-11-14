package com.kyonggi.diet.Food.domain;

import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@SuperBuilder
public class ESquareFood extends ExtendedFood {

    @Id
    @GeneratedValue
    @Column(name = "e_square_food_id")
    private Long id;

    private Long price;

    @Enumerated(EnumType.STRING)
    private ESquareCategory category;

    @Column(name = "category_kr")
    private String categoryKorean;

    @OneToMany(
            mappedBy = "eSquareFood",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ESquareFoodReview> eSquareFoodReviews;
}
