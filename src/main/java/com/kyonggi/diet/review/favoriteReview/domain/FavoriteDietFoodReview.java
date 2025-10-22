package com.kyonggi.diet.review.favoriteReview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.DietFoodReview;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class FavoriteDietFoodReview extends FavoriteReview{

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "diet_food_review_id")
    private DietFoodReview dietFoodReview;
}
