package com.kyonggi.diet.dietContent.service;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContent;

import java.time.LocalDate;
import java.util.List;

public interface DietContentService {

    /**
     * DietContent 엔티티 조회
     * @param id (Long)
     * @return dietContent (DietContent)
     */
    public DietContent findOne(Long id);


    /**
     * 저장 메서드
     * @param dietContentDTO (DietContentDTO)
     */
    public void save(DietContentDTO dietContentDTO);


    /**
     * DietContent DTO 조회
     * @param id (Long)
     * @return DietContentDTO
     */
    public DietContentDTO findDietContent(Long id);


    /**
     * DietContent DTO 리스트 조회
     * @return List<DietContentDTO>
     */
    public List<DietContentDTO> findAll();


    /**
     * DietContent 삭제 메서드
     * @param dietContent (DietContent)
     */
    public void delete(DietContent dietContent);


    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek   (LocalDate)
     * @return List<DietContentDTO>
     */
    public List<DietContentDTO> findDietContentsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek);
}
