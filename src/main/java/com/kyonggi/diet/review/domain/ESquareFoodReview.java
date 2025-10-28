package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ESquareFoodReview extends Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_square_food_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "e_square_food_id",
            foreignKey = @ForeignKey(name = "fk_esquare_food_review_food")
    )
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ESquareFood eSquareFood;
}
