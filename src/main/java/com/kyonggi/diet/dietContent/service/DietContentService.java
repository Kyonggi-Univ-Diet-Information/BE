package com.kyonggi.diet.dietContent.service;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContent;

import java.time.LocalDate;
import java.util.List;

public interface DietContentService {

    /**
     * Diet 엔티티 조회
     * @param id (Long)
     * @return diet (Diet)
     */
    public DietContent findOne(Long id);


    /**
     * 저장 메서드
     * @param dietContentDTO (DietDTO)
     */
    public void save(DietContentDTO dietContentDTO);


    /**
     * Diet DTO 조회
     * @param id (Long)
     * @return DietDTO
     */
    public DietContentDTO findDiet(Long id);


    /**
     * Diet DTO 리스트 조회
     * @return List<DietDTO>
     */
    public List<DietContentDTO> findAll();


    /**
     * Diet 삭제 메서드
     * @param dietContent (Diet)
     */
    public void delete(DietContent dietContent);


    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek   (LocalDate)
     * @return List<Diet>
     */
    public List<DietContentDTO> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek);
}
