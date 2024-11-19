package com.kyonggi.diet.review.favoriteReview.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteRestaurantReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface FavoriteRestaurantReviewRepository extends JpaRepository<FavoriteRestaurantReview, Long> {
    @Query("select f from FavoriteRestaurantReview f where f.member = :member")
    List<FavoriteRestaurantReview> findFavoriteRestaurantReviewListByMember(@Param("member") MemberEntity member);

    @Query("select f.id from FavoriteRestaurantReview f where f.member = :member and f.restaurantReview.id = :reviewId")
    public Long validateThisIsMine(@Param("member") MemberEntity member,
                                   @Param("reviewId") Long reviewId);
}
