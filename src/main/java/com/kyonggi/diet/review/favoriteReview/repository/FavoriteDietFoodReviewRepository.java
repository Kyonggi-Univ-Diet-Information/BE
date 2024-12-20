package com.kyonggi.diet.review.favoriteReview.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteDietFoodReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteDietFoodReviewRepository extends JpaRepository<FavoriteDietFoodReview, Long> {

    @Query("select f from FavoriteDietFoodReview f where f.member = :member")
    public List<FavoriteDietFoodReview> findFavoriteDietFoodReviewListByMember(@Param("member") MemberEntity member);

    @Query("select f.id from FavoriteDietFoodReview f where f.member = :member and f.dietFoodReview.id = :reviewId")
    public Long validateThisIsMine(@Param("member") MemberEntity member,
                                   @Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(f) FROM FavoriteDietFoodReview f WHERE f.dietFoodReview.id = :reviewId")
    Long getCountOfFavorite(@Param("reviewId") Long reviewId);
}
