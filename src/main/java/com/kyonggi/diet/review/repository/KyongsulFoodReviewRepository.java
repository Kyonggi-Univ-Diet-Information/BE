package com.kyonggi.diet.review.repository;

import com.kyonggi.diet.review.domain.DietFoodReview;
import com.kyonggi.diet.review.domain.KyongsulFoodReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KyongsulFoodReviewRepository extends JpaRepository<KyongsulFoodReview, Long> {
    @Query("select avg(r.rating) from KyongsulFoodReview r where r.kyongsulFood.id = :id")
    Double findAverageRatingByKyongsulFoodId(@Param("id") Long id);

    @Query("select r from KyongsulFoodReview r where r.kyongsulFood.id = :kyongsulFoodId")
    Page<KyongsulFoodReview> findAllByKyongsulFoodId(Long kyongsulFoodId, Pageable pageable);

    @Query("select r.rating, count(r) from KyongsulFoodReview r where r.kyongsulFood.id = :id group by r.rating")
    List<Object[]> findRatingCountByKyongsulFoodId(@Param("id") Long id);

    @Query("select count(r) from KyongsulFoodReview r where r.kyongsulFood.id = :id")
    int getKyongsulReviewCount(@Param("id") Long id);

    @Query("select r.kyongsulFood.id, r.kyongsulFood.name, r.kyongsulFood.nameEn from KyongsulFoodReview r where r.kyongsulFood.id = :id")
    Optional<Object[]> findNameByKyongsulFoodId(@Param("id") Long id);
}
