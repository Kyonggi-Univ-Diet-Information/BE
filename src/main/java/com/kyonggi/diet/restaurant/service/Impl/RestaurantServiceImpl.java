package com.kyonggi.diet.restaurant.service.Impl;

import com.kyonggi.diet.restaurant.Restaurant;
import com.kyonggi.diet.restaurant.RestaurantDTO;
import com.kyonggi.diet.restaurant.RestaurantRepository;
import com.kyonggi.diet.restaurant.RestaurantType;
import com.kyonggi.diet.restaurant.service.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    /**
     * Restaurant 저장 메서드
     * @param restaurant (Restaurant)
     */
    @Transactional
    @Override
    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    /**
     * Restaurant 엔티티 조회
     * @param id (Long)
     * @return Restaurant (restaurant)
     */
    @Override
    public Restaurant findOne(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Restaurant not found with id: " + id));
    }

    /**
     * Restaurant DTO 조회
     * @param id (Long)
     * @return RestaurantDTO (RestaurantDTO)
     */
    @Override
    public RestaurantDTO findRestaurant(Long id) {
        Restaurant restaurant = findOne(id);
        return mapToRestaurantDTO(restaurant);
    }

    /**
     * Restaurant DTO 리스트 조회
     * @return List<RestaurantDTO>
     */
    @Override
    public List<RestaurantDTO> findAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) {
            throw new EntityNotFoundException("No Restaurant found");
        }
        return restaurants.stream()
                .map(this::mapToRestaurantDTO)
                .collect(Collectors.toList());
    }

    /**
     * 기숙사의 Restaurant DTO 조회
     * @return RestaurantDTO
     */
    @Override
    public RestaurantDTO findDormitory() {
        Restaurant restaurant = restaurantRepository.findRestaurantByRestaurantType(RestaurantType.DORMITORY);
        return mapToRestaurantDTO(restaurant);
    }

    /**
     * 타입으로 Restaurant 엔티티 조회
     * @param type (RestaurantType)
     * @return Restaurant
     */
    @Override
    public Restaurant findRestaurantByType(RestaurantType type) {
        return restaurantRepository.findRestaurantByRestaurantType(type);
    }

    /**
     * Restaurant -> RestaurantDTO
     * @param restaurant (Restaurant)
     * @return RestaurantDTO (RestaurantDTO)
     */
    private RestaurantDTO mapToRestaurantDTO(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantDTO.class);
    }

}
