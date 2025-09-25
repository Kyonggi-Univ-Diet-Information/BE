package com.kyonggi.diet.kyongsul;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KyongsulFoodRepository extends JpaRepository<KyongsulFood, Long> {

    List<KyongsulFood> findBySubRestaurant(SubRestaurant subRestaurant);

    Optional<KyongsulFood> findByNameAndSubRestaurant(String name, SubRestaurant subRestaurant);
}
