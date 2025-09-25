package com.kyonggi.diet.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.kyongsul.KyongsulFood;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KyongsulFoodReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kyongsul_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "kyongsul_food_id")
    private KyongsulFood kyongsulFood;

    private double rating; //별점[1~5]
    private String title; //제목
    private String content; //내용

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void updateReview(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }
}
