package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.ESquareFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ESquareFoodReviewRepository extends JpaRepository<ESquareFoodReview, Long> {

    @Query("select r from ESquareFoodReview r where r.eSquareFood.id = :id")
    List<ESquareFoodReview> findListById(@Param("id") Long foodId);

    @Query("select avg(r.rating) from ESquareFoodReview r where r.eSquareFood.id = :id")
    Double findAverageRatingByESquareFoodId(@Param("id") Long id);

    @Query("select r from ESquareFoodReview r where r.eSquareFood.id = :eSquareFoodId")
    Page<ESquareFoodReview> findAllByESquareFoodId(Long eSquareFoodId, Pageable pageable);

    @Query("select r.rating, count(r) from ESquareFoodReview r where r.eSquareFood.id = :id group by r.rating")
    List<Object[]> findRatingCountByESquareFoodId(@Param("id") Long id);

    @Query("select count(r) from ESquareFoodReview r where r.eSquareFood.id = :id")
    int getESquareReviewCount(@Param("id") Long id);

    @Query("select r.eSquareFood.id, r.eSquareFood.name, r.eSquareFood.nameEn from ESquareFoodReview r where r.eSquareFood.id = :id")
    Optional<Object[]> findNameByESquareFoodId(@Param("id") Long id);

    @Query("""
        SELECT f.id, r.id, r.rating, r.title, r.content, r.member.id
        FROM ESquareFoodReview r
        JOIN r.eSquareFood f
        ORDER BY r.createdAt DESC
    """)
    List<Object[]> find5ESquareFoodReviewsRecent(Pageable pageable);

    @Query("""
                select r
                from ESquareFoodReview r
                where r.eSquareFood.id = :foodId
                  and (:blockedIds is null or r.member.id not in :blockedIds)
            """)
    Page<ESquareFoodReview> findAllExcludeBlocked(
            Long foodId,
            List<Long> blockedIds,
            Pageable pageable
    );

    List<ESquareFoodReview> findAllByMember(MemberEntity member);

    Page<ESquareFoodReview> findAllByMember(MemberEntity member, Pageable pageable);
}
