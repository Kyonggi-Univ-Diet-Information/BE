package com.kyonggi.diet.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("select r from Restaurant r where r.restaurantType = :type")
    public Restaurant findRestaurantByRestaurantType(@Param("type") RestaurantType type);

}
