package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.Food.domain.DietFood;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@SuperBuilder
@AllArgsConstructor
public class DietFoodReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_food_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "diet_food_id",
            foreignKey = @ForeignKey(name = "fk_diet_food_review_food")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private DietFood dietFood;
}
