package com.kyonggi.diet.review.favoriteReview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.review.domain.SallyBoxFoodReview;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@SuperBuilder
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class FavoriteSallyBoxFoodReview extends FavoriteReview {
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(
            name = "sally_box_food_review_id",
            foreignKey = @ForeignKey(name = "fk_favorite_sally_box_food_review")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SallyBoxFoodReview sallyBoxFoodReview;
}
