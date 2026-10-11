package com.kyonggi.diet.review.image.domain;

import com.kyonggi.diet.review.domain.KyongsulFoodReview;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@SuperBuilder
@AllArgsConstructor
public class KyongsulFoodReviewImage extends ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kyongsul_food_review_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "kyongsul_food_review_id",
            foreignKey = @ForeignKey(name = "fk_kyongsul_food_review_image_review")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KyongsulFoodReview review;
}
