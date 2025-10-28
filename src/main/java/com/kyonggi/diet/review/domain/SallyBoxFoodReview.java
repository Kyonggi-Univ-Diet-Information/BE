package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SallyBoxFoodReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sally_box_food_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sally_box_food_id",
            foreignKey = @ForeignKey(name = "fk_sally_box_food_review_food")
    )
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SallyBoxFood sallyBoxFood;
}
