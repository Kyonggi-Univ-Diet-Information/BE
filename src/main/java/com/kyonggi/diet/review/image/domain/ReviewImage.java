package com.kyonggi.diet.review.image.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@SuperBuilder
@AllArgsConstructor
public abstract class ReviewImage {

    @Column(nullable = false)
    private String imageKey; // S3 key (reviews/{uuid}.webp)

    private int sortOrder;

    @CreationTimestamp
    private Timestamp createdAt;
}
