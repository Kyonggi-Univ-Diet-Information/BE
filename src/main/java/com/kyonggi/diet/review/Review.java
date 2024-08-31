package com.kyonggi.diet.review;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.restaurant.Restaurant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "diet_food_id")
    private DietFood dietFood;


    private double rating; //별점[1~5]
    private String title; //제목
    private String content; //내용


    //==생성==//
    @Builder
    public Review(MemberEntity member, Restaurant restaurant, DietFood dietFood, double rating, String title, String content) {
        this.member = member;
        this.restaurant = restaurant;
        this.dietFood = dietFood;
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    public void updateReview(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }
}
