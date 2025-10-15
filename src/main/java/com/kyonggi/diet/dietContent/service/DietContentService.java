package com.kyonggi.diet.dietContent.service;

import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface DietContentService {

    /**
     * 저장 메서드
     * @param dietContentDTO (DietContentDTO)
     */
    public void save(DietContentDTO dietContentDTO);

    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek   (LocalDate)
     * @return List<DietContentDTO>
     */
    public List<DietContentDTO> findDietContentsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek);

    /**
         * 요일별 기숙사 식당 식단 조회
         * @param targetDay (DayOfWeek)
         * @param startOfWeek (LocalDate)
         * @param endOfWeek (LocalDate)
         */
        List<DietContentDTO> findDietContentsAtDay(DayOfWeek targetDay, LocalDate startOfWeek, LocalDate endOfWeek);
}
