package com.kyonggi.diet.dietFood;

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
}
