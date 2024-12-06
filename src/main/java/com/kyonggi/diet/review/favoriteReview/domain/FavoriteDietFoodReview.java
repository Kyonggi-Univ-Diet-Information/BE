package com.kyonggi.diet.review.favoriteReview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.DietFoodReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDietFoodReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "diet_food_review_id")
    private DietFoodReview dietFoodReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @CreationTimestamp
    private Timestamp createdAt;
}
