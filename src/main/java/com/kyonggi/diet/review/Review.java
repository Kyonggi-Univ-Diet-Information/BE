package com.kyonggi.diet.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private MemberEntity member;

    private double rating; //별점[1~5]
    private String title; //제목
    private String content; //내용

    //==생성 메서드==//
    public static Review createReview(MemberEntity member, double rating, String title, String content) {
        Review review = new Review();
        review.setMember(member);
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        return review;
    }
}
