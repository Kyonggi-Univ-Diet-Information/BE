package com.kyonggi.diet.Food.repository;

import com.kyonggi.diet.Food.domain.KyongsulSetFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KyongsulSetFoodRepository extends JpaRepository<KyongsulSetFood,Long> {

    @Query("select k from KyongsulSetFood k where k.baseFood.id = :baseFoodId")
    List<KyongsulSetFood> findAllByBaseFoodId(@Param("baseFoodId") Long baseFoodId);

    Optional<KyongsulSetFood> findByName(String name);
}
