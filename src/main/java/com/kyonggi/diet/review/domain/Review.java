package com.kyonggi.diet.review.domain;

import com.kyonggi.diet.review.keyword.KeywordLevel;
import com.kyonggi.diet.member.MemberEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@SuperBuilder
@AllArgsConstructor
public abstract class Review {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(name = "fk_review_member")
    )
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MemberEntity member;

    private double rating;     // 별점 [1~5]
    private String title;      // 제목
    private String content;    // 내용

    // 키워드 (카테고리당 최대 1개, 선택 안 함 = null)
    @Enumerated(EnumType.STRING)
    private KeywordLevel saltLevel;       // 간

    @Enumerated(EnumType.STRING)
    private KeywordLevel spicyLevel;     // 맵기

    @Enumerated(EnumType.STRING)
    private KeywordLevel portionLevel; // 양

    @Enumerated(EnumType.STRING)
    private KeywordLevel valueLevel;     // 가성비

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void updateReview(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    /** 키워드 전체 교체. null 은 해당 카테고리 선택 해제를 의미한다. */
    public void updateKeywords(KeywordLevel saltLevel, KeywordLevel spicyLevel,
                               KeywordLevel portionLevel, KeywordLevel valueLevel) {
        this.saltLevel = saltLevel;
        this.spicyLevel = spicyLevel;
        this.portionLevel = portionLevel;
        this.valueLevel = valueLevel;
    }
}
