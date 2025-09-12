package com.kyonggi.diet.dietFood.service;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.DietFoodType;

import java.util.List;

public interface DietFoodService {

    /**
     * DietFood 엔티티 조회
     * @param id (Long)
     * @return DietFood
     */
    public DietFood findOne(Long id);


    /**
     * 저장 메서드
     * @param dietFoodDTO (DietFoodDTO)
     */
    public DietFood save(DietFoodDTO dietFoodDTO);

    /**
     * DietFood DTO 조회
     * @param id (Long)
     * @return DietFoodDTO
     */
    public DietFoodDTO findDietFood(Long id);


    /**
     * DietFood DTO 리스트 조회
     * @return List<DietFoodDTO>
     */
    public List<DietFoodDTO> findAll();


    /**
     * DietFood 타입[RICE, SIDE, SOUP, DESERT] 에 맞는 DTO 리스트 조회
     * @param type (DietFoodType)
     * @return dietFoodDTOS (List<DietFoodDTO>)
     */
    public List<DietFoodDTO> findDietFoodByType(DietFoodType type);


    /**
     * Id 리스트들로 DietFood DTO 리스트 조회
     * @param IdList (List<Long>)
     * @return findDietFoods (List<DietFoodDTO>)
     */
    public List<DietFoodDTO> findDietFoodsByIdList(List<Long> IdList);

    /**
     * DietFoodDTO -> DietFood 엔티티로 전환
     * @param DTO (DietFoodDTO)
     * @return DietFood
     */
    public DietFood convertToEntity(DietFoodDTO DTO);

    /**
     * 이름으로 음식 찾기
     * @param name (String0
     * @return DietFood
     */
    public DietFood findDietFoodByName(String name);

    /**
     * 음식 이름으로 DB에 존재 여부
     * @param name (String)
     * @return Boolean
     */
    boolean checkExistByName(String name);
}
