package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KyongsulFoodRepository extends JpaRepository<KyongsulFood, Long> {

    List<KyongsulFood> findBySubRestaurant(SubRestaurant subRestaurant);

    Optional<KyongsulFood> findByNameAndSubRestaurant(String name, SubRestaurant subRestaurant);

    @Query("select new com.kyonggi.diet.review.DTO.FoodNamesDTO(k.id, k.name, k.nameEn) from KyongsulFood k where k.id = :id")
    Optional<FoodNamesDTO> findNameByKyongsulFoodId(@Param("id") Long id);

    Optional<KyongsulFood>  findByName(String name);
}
