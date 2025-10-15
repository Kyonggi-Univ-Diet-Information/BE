package com.kyonggi.diet.review.favoriteReview.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteKyongsulFoodReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteKyongsulFoodReviewRepository extends JpaRepository<FavoriteKyongsulFoodReview, Long> {
    @Query("select f from FavoriteKyongsulFoodReview f where f.member = :member")
    public List<FavoriteKyongsulFoodReview> findFavoriteKyongsulFoodReviewListByMember(@Param("member") MemberEntity member);

    @Query("select f.id from FavoriteKyongsulFoodReview f where f.member = :member and f.kyongsulFoodReview.id = :reviewId")
    public Long validateThisIsMine(@Param("member") MemberEntity member,
                                   @Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(f) FROM FavoriteKyongsulFoodReview f WHERE f.kyongsulFoodReview.id = :reviewId")
    Long getCountOfFavorite(@Param("reviewId") Long reviewId);

    @Query("""
        SELECT 
            f.id, r.id, r.rating, r.title, r.content, r.member.id, COUNT(fr.id)
        FROM KyongsulFoodReview r
        JOIN r.kyongsulFood f
        LEFT JOIN FavoriteKyongsulFoodReview fr ON fr.kyongsulFoodReview = r
        GROUP BY f.id, r.id, r.rating, r.title, r.content, r.member.id
        ORDER BY COUNT(fr.id) DESC
    """)
    List<Object[]> findTop5KyongsulByMostFavorited(Pageable pageable);

    List<FavoriteKyongsulFoodReview> findAllByMember(MemberEntity member);
}
