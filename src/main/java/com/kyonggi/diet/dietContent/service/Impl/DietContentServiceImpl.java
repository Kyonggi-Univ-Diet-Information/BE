package com.kyonggi.diet.dietContent.service.Impl;

import com.kyonggi.diet.diet.DietRepository;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContent;
import com.kyonggi.diet.dietContent.DietContentRepository;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DietContentServiceImpl implements DietContentService {

    private final DietContentRepository dietContentRepository;
    private final DietFoodService dietFoodService;
    private final DietRepository dietRepository;
    private final ModelMapper modelMapper;

    /**
     * Diet 엔티티 조회
     * @param id (Long)
     * @return diet (Diet)
     */
    @Override
    public DietContent findOne(Long id) {
        return dietContentRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Diet not found with id: " + id));
    }

    /**
     * 저장 메서드
     * @param dietContentDTO (DietDTO)
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

    private Diet convertToEntity(DietDTO dto, DietContent dietContent){
        DietFood dietFood = dietFoodService.convertToEntity(dto.getDietFoodDTO());
        return Diet.builder()
                            .dietContent(dietContent)
                            .dietFood(dietFood)
                            .build();
    }


    /**
     * Diet DTO 조회
     * @param id (Long)
     * @return DietDTO
     */
    @Override
    public DietContentDTO findDiet(Long id) {
        DietContent dietContent = findOne(id);
        return mapToDietDTO(dietContent);
    }

    /**
     * Diet DTO 리스트 조회
     * @return List<DietDTO>
     */
    @Override
    public List<DietContentDTO> findAll() {
        List<DietContent> dietContents = dietContentRepository.findAll();
        if (dietContents.isEmpty()) {
            throw new EntityNotFoundException("Diets not found");
        }
        return dietContents.stream()
                .map(this::mapToDietDTO)
                .collect(Collectors.toList());
    }

    /**
     * Diet 삭제 메서드
     * @param dietContent (Diet)
     */
    @Transactional
    @Override
    public void delete(DietContent dietContent) {
        dietContentRepository.delete(dietContent);
    }

    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek (LocalDate)
     * @return List<Diet>
     */
    @Override
    public List<DietContentDTO> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<DietContent> dietContents = dietContentRepository.findDietsBetweenDates(startOfWeek.toString(), endOfWeek.toString());
        if (dietContents.isEmpty()) {
            throw new EntityNotFoundException("Can't find diets between start of week and end of week");
        }
        return dietContents.stream()
                .map(this::mapToDietDTO)
                .collect(Collectors.toList());
    }

    /**
     * Diet -> DietDTO
     * @param dietContent (Diet)
     * @return DietDTO (DietDTO)
     */
    private DietContentDTO mapToDietDTO(DietContent dietContent) {
        return modelMapper.map(dietContent, DietContentDTO.class);
    }
}
