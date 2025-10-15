package com.kyonggi.diet.dietContent.service.Impl;

import com.kyonggi.diet.diet.DietRepository;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContent;
import com.kyonggi.diet.dietContent.DietContentRepository;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.service.DietFoodReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DietContentServiceImpl implements DietContentService {

    private final DietContentRepository dietContentRepository;
    private final DietFoodReviewService dietFoodReviewService;
    private final DietRepository dietRepository;
    private final DietFoodService dietFoodService;
    private final ModelMapper modelMapper;

    /**
     * 저장 메서드
     *
     * @param dietContentDTO (DietContentDTO)
     */
    @Transactional
    @Override
    public void save(DietContentDTO dietContentDTO) {
        DietContent dietContent = DietContent.builder()
                .date(dietContentDTO.getDate())
                .time(dietContentDTO.getTime())
                .build();
        dietContentRepository.save(dietContent);

        List<Diet> findContents = dietContentDTO.getContents().stream()
                .map(data -> convertToEntity(data, dietContent))
                .collect(Collectors.toList());
        dietRepository.saveAll(findContents);
    }

    /**
     * DietDto ->  Diet Entity
     *
     * @param dto         (DietDTO)
     * @param dietContent (DietContent)
     * @return Diet
     */
    private Diet convertToEntity(DietDTO dto, DietContent dietContent) {
        DietFood dietFood = dietFoodService.convertToEntity(dto.getDietFoodDTO());
        return Diet.builder()
                .dietContent(dietContent)
                .dietFood(dietFood)
                .build();
    }

    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek   (LocalDate)
     * @return List<DietContentDTO>
     */
    @Override
    public List<DietContentDTO> findDietContentsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<DietContent> dietContents = dietContentRepository
                .findDietsBetweenDates(startOfWeek.toString(), endOfWeek.toString());
        if (dietContents.isEmpty()) {
            throw new EntityNotFoundException("Can't find dietContents between start of week and end of week");
        }

        return dietContents.stream().map(dietContent -> {
            List<DietDTO> dietDTOs = dietContent.getContents().stream().map(diet -> {
                DietFoodDTO dietFoodDTO = DietFoodDTO.builder()
                        .id(diet.getDietFood().getId())
                        .name(diet.getDietFood().getName())
                        .type(diet.getDietFood().getDietFoodType())
                        .nameEn(diet.getDietFood().getNameEn())
                        .build();

                return DietDTO.builder()
                        .id(diet.getId())
                        .dietFoodDTO(dietFoodDTO).build();
            }).collect(Collectors.toList());

            DietContentDTO dietContentDTO = mapToDietContentDTO(dietContent);
            dietContentDTO.setContents(dietDTOs);

            return dietContentDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 요일별 기숙사 식당 식단 조회
     * @param targetDay (DayOfWeek)
     * @param startOfWeek (LocalDate)
     * @param endOfWeek (LocalDate)
     */
    @Override
    public List<DietContentDTO> findDietContentsAtDay(DayOfWeek targetDay, LocalDate startOfWeek, LocalDate endOfWeek) {

        int postgresDOW = targetDay.getValue() % 7;

        List<DietContent> dietContents = dietContentRepository
                .findByDayOfWeek(startOfWeek, endOfWeek, postgresDOW);

        if (dietContents.isEmpty()) {
            throw new EntityNotFoundException("No diet contents found on " + targetDay.name());
        }

        return dietContents.stream().map(dietContent -> {
            List<DietDTO> dietDTOs = dietContent.getContents().stream().map(diet -> {
                DietFoodDTO dietFoodDTO = DietFoodDTO.builder()
                        .id(diet.getDietFood().getId())
                        .name(diet.getDietFood().getName())
                        .type(diet.getDietFood().getDietFoodType())
                        .nameEn(diet.getDietFood().getNameEn())
                        .build();

                return DietDTO.builder()
                        .id(diet.getId())
                        .dietFoodDTO(dietFoodDTO)
                        .build();
            }).collect(Collectors.toList());

            DietContentDTO dietContentDTO = mapToDietContentDTO(dietContent);
            dietContentDTO.setContents(dietDTOs);

            return dietContentDTO;
        }).collect(Collectors.toList());
    }


    /**
     * DietContent -> DietContentDTO
     *
     * @param dietContent (DietContent)
     * @return DietContentDTO (DietContentDTO)
     */
    private DietContentDTO mapToDietContentDTO(DietContent dietContent) {
        return modelMapper.map(dietContent, DietContentDTO.class);
    }
}


