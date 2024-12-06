package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class DietFoodReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_food_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "diet_food_id")
    private DietFood dietFood;

    private double rating; //별점[1~5]
    private String title; //제목
    private String content; //내용

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;


    //==생성==//
    @Builder
    public DietFoodReview(MemberEntity member, DietFood dietFood, double rating, String title, String content, Timestamp createdAt, Timestamp updatedAt) {
        this.member = member;
        this.dietFood = dietFood;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateReview(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }
}
