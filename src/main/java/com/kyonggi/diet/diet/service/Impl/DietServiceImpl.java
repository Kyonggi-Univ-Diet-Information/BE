package com.kyonggi.diet.diet.service.Impl;

import com.kyonggi.diet.diet.DTO.DietDTO;
import com.kyonggi.diet.diet.Diet;
import com.kyonggi.diet.diet.DietRepository;
import com.kyonggi.diet.diet.service.DietService;
import com.kyonggi.diet.dietContent.DietContent;
import com.kyonggi.diet.dietContent.DietContentDTO;
import com.kyonggi.diet.dietContent.DietContentRepository;
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
public class DietServiceImpl implements DietService {

    private final DietRepository dietRepository;
    private final DietFoodService dietFoodService;
    private final DietContentRepository dietContentRepository;
    private final ModelMapper modelMapper;

    /**
     * Diet 엔티티 조회
     * @param id (Long)
     * @return diet (Diet)
     */
    @Override
    public Diet findOne(Long id) {
        return dietRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Diet not found with id: " + id));
    }

    /**
     * 저장 메서드
     * @param dietDTO (DietDTO)
     */
    @Transactional
    @Override
    public void save(DietDTO dietDTO) {
        Diet diet = Diet.builder()
                                    .date(dietDTO.getDate())
                                    .time(dietDTO.getTime())
                                    .build();
        dietRepository.save(diet);

        List<DietContent> findContents = dietDTO.getContents().stream()
                        .map(data -> convertToEntity(data, diet))
                        .collect(Collectors.toList());
        dietContentRepository.saveAll(findContents);
    }

    private DietContent convertToEntity(DietContentDTO dto, Diet diet){
        DietFood dietFood = dietFoodService.convertToEntity(dto.getDietFoodDTO());
        return DietContent.builder()
                            .diet(diet)
                            .dietFood(dietFood)
                            .build();
    }


    /**
     * Diet DTO 조회
     * @param id (Long)
     * @return DietDTO
     */
    @Override
    public DietDTO findDiet(Long id) {
        Diet diet = findOne(id);
        return mapToDietDTO(diet);
    }

    /**
     * Diet DTO 리스트 조회
     * @return List<DietDTO>
     */
    @Override
    public List<DietDTO> findAll() {
        List<Diet> diets = dietRepository.findAll();
        if (diets.isEmpty()) {
            throw new EntityNotFoundException("Diets not found");
        }
        return diets.stream()
                .map(this::mapToDietDTO)
                .collect(Collectors.toList());
    }

    /**
     * Diet 삭제 메서드
     * @param diet (Diet)
     */
    @Transactional
    @Override
    public void delete(Diet diet) {
        dietRepository.delete(diet);
    }

    /**
     * 이번 주의 Diet 조회
     * @param startOfWeek (LocalDate)
     * @param endOfWeek (LocalDate)
     * @return List<Diet>
     */
    @Override
    public List<DietDTO> findDietsBetweenDates(LocalDate startOfWeek, LocalDate endOfWeek) {
        List<Diet> diets = dietRepository.findDietsBetweenDates(startOfWeek.toString(), endOfWeek.toString());
        if (diets.isEmpty()) {
            throw new EntityNotFoundException("Can't find diets between start of week and end of week");
        }
        return diets.stream()
                .map(this::mapToDietDTO)
                .collect(Collectors.toList());
    }

    /**
     * Diet -> DietDTO
     * @param diet (Diet)
     * @return DietDTO (DietDTO)
     */
    private DietDTO mapToDietDTO(Diet diet) {
        return modelMapper.map(diet, DietDTO.class);
    }
}
