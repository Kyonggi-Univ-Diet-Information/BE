package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.DietFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DietFoodReviewRepository extends JpaRepository<DietFoodReview, Long> {
    @Query("select r from DietFoodReview r where r.dietFood.id = :id")
    List<DietFoodReview> findListById(@Param("id") Long dietFoodId);

    @Query("select avg(r.rating) from DietFoodReview r where r.dietFood.id = :id")
    Double findAverageRatingByDietFoodId(@Param("id") Long id);

    @Query("select r from DietFoodReview r where r.dietFood.id = :dietFoodId")
    Page<DietFoodReview> findAllByDietFoodId(Long dietFoodId, Pageable pageable);

    @Query("select r.rating, count(r) from DietFoodReview r where r.dietFood.id = :id group by r.rating")
    List<Object[]> findRatingCountByDietFoodId(@Param("id") Long id);

    @Query("select count(r) from DietFoodReview r where r.dietFood.id = :id")
    int getDietFoodReviewCount(@Param("id") Long id);

    @Query("""
        SELECT f.id, r.id, r.rating, r.title, r.content, r.member.id
        FROM DietFoodReview r
        JOIN r.dietFood f
        ORDER BY r.createdAt DESC
    """)
    List<Object[]> find5DietFoodReviewsRecent(Pageable pageable);

    List<DietFoodReview> findAllByMember(MemberEntity member);

    Page<DietFoodReview> findAllByMember(MemberEntity member, Pageable pageable);
}
