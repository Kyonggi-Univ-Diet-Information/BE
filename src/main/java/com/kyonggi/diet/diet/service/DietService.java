package com.kyonggi.diet.diet.service;

import com.kyonggi.diet.diet.DTO.DietDTO;
import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import java.time.LocalDate;
import java.util.List;

public interface DietService {

    /**
     * Diet 엔티티 조회
     * @param id (Long)
     * @return diet (Diet)
     */
    public Diet findOne(Long id);


    /**
     * 저장 메서드
     * @param dietDTO (DietDTO)
     */
    public void save(DietDTO dietDTO);


    /**
     * Diet DTO 조회
     * @param id (Long)
     * @return DietDTO
     */
    public DietDTO findDiet(Long id);


    /**
     * Diet DTO 리스트 조회
     * @return List<DietDTO>
     */
    public List<DietDTO> findAll();


    /**
     * Diet 삭제 메서드
     * @param diet (Diet)
     */
    public void delete(Diet diet);


    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek   (LocalDate)
     * @return List<Diet>
     */
    public List<DietDTO> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek);
}
