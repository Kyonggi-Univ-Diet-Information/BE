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
     * DietContent -> DietContentDTO
     *
     * @param dietContent (DietContent)
     * @return DietContentDTO (DietContentDTO)
     */
    private DietContentDTO mapToDietContentDTO(DietContent dietContent) {
        return modelMapper.map(dietContent, DietContentDTO.class);
    }
}


/*
* @Override
@Transactional(readOnly = true)
public List<DietContentDTO> findDietContentsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {
    List<DietContent> contents = dietContentRepository.findBetween(startOfWeek, endOfWeek);

    if (contents.isEmpty()) {
        // 비즈니스 정책에 따라 선택: 빈 리스트 반환 or 예외
        return Collections.emptyList();
        // throw new EntityNotFoundException("No diet contents in range");
    }

    // 1) 모든 DietFood ID를 한 번에 수집
    Set<Long> foodIds = contents.stream()
        .flatMap(dc -> dc.getContents().stream())
        .map(d -> d.getDietFood().getId())
        .collect(Collectors.toSet());

    // 2) 리뷰를 일괄 조회하여 맵으로 (foodId -> reviews)
    Map<Long, List<ReviewDTO>> reviewsByFoodId =
        dietFoodReviewService.findMapByFoodIds(foodIds);

    // 3) 매핑
    return contents.stream().map(dc -> {
        List<DietDTO> dietDTOs = dc.getContents().stream().map(diet -> {
            var food = diet.getDietFood();
            var reviewDTOs = reviewsByFoodId.getOrDefault(food.getId(), List.of());

            DietFoodDTO dietFoodDTO = DietFoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .type(food.getDietFoodType())
                .dietFoodReviews(reviewDTOs)
                .build();

            return DietDTO.builder()
                .id(diet.getId())
                .dietFoodDTO(dietFoodDTO)
                .build();
        }).toList();

        DietContentDTO dto = mapToDietContentDTO(dc);
        dto.setContents(dietDTOs);
        return dto;
    }).toList();
}
*/
