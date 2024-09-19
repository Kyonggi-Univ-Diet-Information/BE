package com.kyonggi.diet.review.favoriteReview.DTO;

import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.DietFoodReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDietFoodReviewDTO {

    private Long id;

    private MemberDTO memberDTO;

    private Timestamp createdAt;
}
