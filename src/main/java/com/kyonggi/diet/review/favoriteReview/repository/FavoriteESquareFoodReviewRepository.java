package com.kyonggi.diet.review.favoriteReview.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.favoriteReview.domain.FavoriteESquareFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteESquareFoodReviewRepository extends JpaRepository<FavoriteESquareFoodReview, Long> {

    @Query("select f from FavoriteESquareFoodReview f where f.member = :member")
    List<FavoriteESquareFoodReview> findFavoriteESquareFoodReviewListByMember(@Param("member") MemberEntity member);

    @Query("select f.id from FavoriteESquareFoodReview f where f.member = :member and f.esquareFoodReview.id = :reviewId")
    Long validateThisIsMine(@Param("member") MemberEntity member,
                                   @Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(f) FROM FavoriteESquareFoodReview f WHERE f.esquareFoodReview.id = :reviewId")
    Long getCountOfFavorite(@Param("reviewId") Long reviewId);

    @Query("""
        SELECT 
            f.id, r.id, r.rating, r.title, r.content, r.member.id, COUNT(fr.id)
        FROM ESquareFoodReview r
        JOIN r.eSquareFood f
        LEFT JOIN FavoriteESquareFoodReview fr ON fr.esquareFoodReview = r
        GROUP BY f.id, r.id, r.rating, r.title, r.content, r.member.id
        ORDER BY COUNT(fr.id) DESC
    """)
    List<Object[]> findTop5ESquareByMostFavorited(Pageable pageable);

    List<FavoriteESquareFoodReview> findAllByMember(MemberEntity member);

    Page<FavoriteESquareFoodReview> findAllByMember(MemberEntity member, Pageable pageable);
}
