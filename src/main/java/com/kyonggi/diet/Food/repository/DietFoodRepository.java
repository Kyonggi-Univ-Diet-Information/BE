package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.domain.DietFood;
import com.kyonggi.diet.Food.eumer.DietFoodType;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;


public interface DietFoodRepository extends JpaRepository<DietFood, Long> {

    @Query("select d from DietFood d where d.dietFoodType = :dietType")
    public List<DietFood> findDietFoodListByType(@Param("dietType") DietFoodType type);

    @Query("select d from DietFood d where d.name = :name")
    public DietFood findDietFoodByName(@Param("name") String name);

    Optional<DietFood> findByName(String name);

    @Query("select new com.kyonggi.diet.review.DTO.FoodNamesDTO(d.id, d.name, d.nameEn) from DietFood d where d.id = :id")
    Optional<FoodNamesDTO> findNameByDietFoodId(@Param("id") Long id);
}
