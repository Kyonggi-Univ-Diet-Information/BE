package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ESquareFoodRepository extends JpaRepository<ESquareFood, Long> {

    Optional<ESquareFood> findByName(String name);

    @Query("select new com.kyonggi.diet.review.DTO.FoodNamesDTO(e.id, e.name, e.nameEn) from ESquareFood e where e.id = :id")
    Optional<FoodNamesDTO> findNameByESquareFoodId(@Param("id") Long id);
}
