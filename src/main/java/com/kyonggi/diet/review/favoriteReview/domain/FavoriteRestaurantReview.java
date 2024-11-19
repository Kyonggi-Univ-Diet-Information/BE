package com.kyonggi.diet.review.favoriteReview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.RestaurantReview;
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
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRestaurantReview {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "restaurant_review_id")
    private RestaurantReview restaurantReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @CreationTimestamp
    private Timestamp createdAt;

}
