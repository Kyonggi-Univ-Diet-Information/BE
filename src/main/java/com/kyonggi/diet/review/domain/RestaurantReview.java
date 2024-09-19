package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.restaurant.Restaurant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RestaurantReview {

    @Id @GeneratedValue
    @Column(name = "restaurant_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private double rating; //별점[1~5]
    private String title; //제목
    private String content; //내용

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;


    //==생성==//
    @Builder
    public RestaurantReview(MemberEntity member, Restaurant restaurant, double rating, String title, String content, Timestamp createdAt, Timestamp updatedAt) {
        this.member = member;
        this.restaurant = restaurant;
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
