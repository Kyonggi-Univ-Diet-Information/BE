package com.kyonggi.diet.review.favoriteReview.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteSallyBoxFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteSallyBoxFoodReviewRepository extends JpaRepository<FavoriteSallyBoxFoodReview, Long> {
    @Query("select f from FavoriteSallyBoxFoodReview f where f.member = :member")
    List<FavoriteSallyBoxFoodReview> findFavoriteSallyBoxFoodReviewListByMember(@Param("member") MemberEntity member);

    @Query("select f.id from FavoriteSallyBoxFoodReview f where f.member = :member and f.sallyBoxFoodReview.id = :reviewId")
    Long validateThisIsMine(@Param("member") MemberEntity member,
                            @Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(f) FROM FavoriteSallyBoxFoodReview f WHERE f.sallyBoxFoodReview.id = :reviewId")
    Long getCountOfFavorite(@Param("reviewId") Long reviewId);

    @Query("""
                SELECT
                    f.id, r.id, r.rating, r.title, r.content, r.member.id, COUNT(fr.id)
                FROM SallyBoxFoodReview r
                JOIN r.sallyBoxFood f
                LEFT JOIN FavoriteSallyBoxFoodReview fr ON fr.sallyBoxFoodReview = r
                GROUP BY f.id, r.id, r.rating, r.title, r.content, r.member.id
                ORDER BY COUNT(fr.id) DESC
            """)
    List<Object[]> findTop5SallyBoxByMostFavorited(Pageable pageable);

    List<FavoriteSallyBoxFoodReview> findAllByMember(MemberEntity member);

    Page<FavoriteSallyBoxFoodReview> findAllByMember(MemberEntity member, Pageable pageable);
}
