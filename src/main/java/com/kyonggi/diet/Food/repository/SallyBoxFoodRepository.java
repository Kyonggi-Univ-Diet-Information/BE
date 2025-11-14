package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SallyBoxFoodRepository extends JpaRepository<SallyBoxFood, Long> {

    Optional<SallyBoxFood> findByName(String name);

    @Query("select new com.kyonggi.diet.review.DTO.FoodNamesDTO(e.id, e.name, e.nameEn) from SallyBoxFood e where e.id = :id")
    Optional<FoodNamesDTO> findNameBySallyBoxFoodId(@Param("id") Long id);

    @Query("""
        SELECT f.id, f.name, f.nameEn, f.price, f.cuisine, f.foodType, f.detailedMenu, count(r)
        FROM SallyBoxFoodReview r
        JOIN r.sallyBoxFood f
        GROUP BY f.id
        HAVING count(r) > 0
        ORDER BY count(r) DESC
    """)
    List<Object[]> find5FoodFavorite(Pageable pageable);
}
