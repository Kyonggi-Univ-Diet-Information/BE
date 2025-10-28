package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.review.domain.SallyBoxFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SallyBoxFoodReviewRepository extends JpaRepository<SallyBoxFoodReview, Long> {
    @Query("select r from SallyBoxFoodReview r where r.sallyBoxFood.id = :id")
    List<SallyBoxFoodReview> findListById(@Param("id") Long foodId);

    @Query("select avg(r.rating) from SallyBoxFoodReview r where r.sallyBoxFood.id = :id")
    Double findAverageRatingBySallyBoxFoodId(@Param("id") Long id);

    @Query("select r from SallyBoxFoodReview r where r.sallyBoxFood.id = :sallyBoxFoodId")
    Page<SallyBoxFoodReview> findAllBySallyBoxFoodId(Long sallyBoxFoodId, Pageable pageable);

    @Query("select r.rating, count(r) from SallyBoxFoodReview r where r.sallyBoxFood.id = :id group by r.rating")
    List<Object[]> findRatingCountBySallyBoxFoodId(@Param("id") Long id);

    @Query("select count(r) from SallyBoxFoodReview r where r.sallyBoxFood.id = :id")
    int getSallyBoxReviewCount(@Param("id") Long id);

    @Query("select r.sallyBoxFood.id, r.sallyBoxFood.name, r.sallyBoxFood.nameEn from SallyBoxFoodReview r where r.sallyBoxFood.id = :id")
    Optional<Object[]> findNameBySallyBoxFoodId(@Param("id") Long id);

    @Query("""
                SELECT f.id, r.id, r.rating, r.title, r.content, r.member.id
                FROM SallyBoxFoodReview r
                JOIN r.sallyBoxFood f
                ORDER BY r.createdAt DESC
            """)
    List<Object[]> find5SallyBoxFoodReviewsRecent(Pageable pageable);

    List<SallyBoxFoodReview> findAllByMember(MemberEntity member);

    Page<SallyBoxFoodReview> findAllByMember(MemberEntity member, Pageable pageable);
}
