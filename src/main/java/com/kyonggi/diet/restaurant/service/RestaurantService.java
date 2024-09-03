package com.kyonggi.diet.restaurant.service;

import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantDTO;
import com.kyonggi.diet.restaurant.RestaurantType;

import java.util.List;

public interface RestaurantService {

    /**
     * Restaurant 저장 메서드
     * @param restaurant (Restaurant)
     */
    public void save(Restaurant restaurant);

    /**
     * Restaurant 엔티티 조회
     * @param id (Long)
     * @return Restaurant (restaurant)
     */
    public Restaurant findOne(Long id);

    /**
     * Restaurant DTO 조회
     * @param id (Long)
     * @return RestaurantDTO (RestaurantDTO)
     */
    public RestaurantDTO findRestaurant(Long id);

    public Restaurant findRestaurantByType(RestaurantType type);

    /**
     * Restaurant DTO 리스트 조회
     * @return List<RestaurantDTO>
     */
    public List<RestaurantDTO> findAll();


    /**
     * 기숙사의 Restaurant DTO 조회
     * @return RestaurantDTO
     */
    public RestaurantDTO findDormitory();

}
