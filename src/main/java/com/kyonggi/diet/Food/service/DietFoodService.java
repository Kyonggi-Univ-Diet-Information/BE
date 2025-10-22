package com.kyonggi.diet.Food.service;

import com.kyonggi.diet.Food.domain.DietFood;
import com.kyonggi.diet.Food.DTO.DietFoodDTO;
import com.kyonggi.diet.Food.repository.DietFoodRepository;
import com.kyonggi.diet.Food.eumer.DietFoodType;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DietFoodService extends AbstractFoodService<DietFood, DietFoodDTO> {

    private final DietFoodRepository dietFoodRepository;

    public DietFoodService(ModelMapper modelMapper, DietFoodRepository dietFoodRepository) {
        super(modelMapper);
        this.dietFoodRepository = dietFoodRepository;
    }

    /**
     * 저장 메서드
     *
     * @param dietFoodDTO (DietFoodDTO)
     */
    @Transactional
    @Override
    public DietFood save(DietFoodDTO dietFoodDTO) {
        try {
            return dietFoodRepository.findByName(dietFoodDTO.getName())
                    .map(existing -> {
                        // ✅ 업데이트 처리
                        if (dietFoodDTO.getNameEn() != null && existing.getNameEn() == null) {
                            existing.updateNameEn(dietFoodDTO.getNameEn());
                        }
                        if (dietFoodDTO.getType() != null) {
                            existing.updateDietFoodType(dietFoodDTO.getType());
                        }
                        return dietFoodRepository.save(existing); // update
                    })
                    .orElseGet(() -> {
                        // ✅ 새로 저장
                        DietFood dietFood = DietFood.builder()
                                .name(dietFoodDTO.getName())
                                .nameEn(dietFoodDTO.getNameEn())
                                .dietFoodType(dietFoodDTO.getType())
                                .build();
                        return dietFoodRepository.save(dietFood);
                    });
        } catch (Exception e) {
            log.error("DietFood 저장 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("DietFood 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * DietFood DTO 조회
     *
     * @param id (Long)
     * @return DietFoodDTO
     */
    @Override
    public DietFoodDTO findById(Long id) {
        DietFood dietFood = dietFoodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("DietFood not found with id:" + id));
        return mapToDto(dietFood, DietFoodDTO.class);
    }

    /**
     * DietFood DTO 리스트 조회
     *
     * @return List<DietFoodDTO>
     */
    @Override
    public List<DietFoodDTO> findAll() {
        List<DietFood> dietFoods = dietFoodRepository.findAll();
        if (dietFoods.isEmpty()) {
            throw new EntityNotFoundException("DietFood not found");
        }
        return mapToDtoList(dietFoods, DietFoodDTO.class);
    }

    /**
     * Id 리스트들로 DietFood DTO 리스트 조회
     *
     * @param idList (List<Long>)
     * @return findDietFoods (List<DietFoodDTO>)
     */
    public List<DietFoodDTO> findDietFoodsByIdList(List<Long> idList) {
        return idList.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    /**
     * 이름으로 음식 찾기
     *
     * @param name (String)
     * @return DietFood
     */
    public DietFood findDietFoodByName(String name) {
        return dietFoodRepository.findDietFoodByName(name);
    }

    /**
     * 음식 이름으로 DB에 존재 여부
     *
     * @param name (String)
     * @return Boolean
     */
    @Override
    public boolean existsByName(String name) {
        return dietFoodRepository.findDietFoodByName(name) != null;
    }

    /**
     * 음식 id로 음식 이름 반환(한국어, 영어)
     */
    @Override
    public FoodNamesDTO findNamesByFoodId(Long foodId) {
        return dietFoodRepository.findNameByDietFoodId(foodId)
                .orElseThrow(() -> new NoSuchElementException("음식(id=" + foodId + ")을 찾을 수 없습니다."));
    }

    public DietFood convertToEntity(DietFoodDTO dto) {
        DietFood dietFood = findOne(dto.getId());
        if (dietFood == null) {
            throw new RuntimeException("DietFood not found with id: " + dto.getId());
        }
        return dietFood;
    }

    private DietFood findOne(Long id) {
        return dietFoodRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("DietFood not found with id:" + id));
    }
}
