package com.kyonggi.diet.review.favoriteReview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class FavoriteESquareFoodReview extends FavoriteReview {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "e_square_food_review_id")
    private ESquareFoodReview esquareFoodReview;
}
