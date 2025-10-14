package com.kyonggi.diet.dietFood.service.Impl;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.DietFoodRepository;
import com.kyonggi.diet.dietFood.DietFoodType;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DietFoodServiceImpl implements DietFoodService {

    private final DietFoodRepository dietFoodRepository;
    private final ModelMapper modelMapper;


    /**
     * DietFood 엔티티 조회
     * @param id (Long)
     * @return DietFood
     */
    @Override
    public DietFood findOne(Long id) {
        return dietFoodRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("DietFood not found with id:" + id));
    }

    /**
     * 저장 메서드
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
     * @param id (Long)
     * @return DietFoodDTO
     */
    @Override
    public DietFoodDTO findDietFood(Long id) {
        DietFood dietFood = findOne(id);
        return  mapToDietFoodDTO(dietFood);
    }

    /**
     * DietFood DTO 리스트 조회
     * @return List<DietFoodDTO>
     */
    @Override
    public List<DietFoodDTO> findAll() {
        List<DietFood> dietFoods = dietFoodRepository.findAll();
        if (dietFoods.isEmpty()) {
            throw new EntityNotFoundException("DietFood not found");
        }
        return dietFoods.stream()
                .map(this::mapToDietFoodDTO)
                .collect(Collectors.toList());
    }

    /**
     * DietFood 타입[RICE, SIDE, SOUP, DESERT] 에 맞는 DTO 리스트 조회
     * @param type (DietFoodType)
     * @return dietFoodDTOS (List<DietFoodDTO>)
     */
    @Override
    public List<DietFoodDTO> findDietFoodByType(DietFoodType type) {
        List<DietFood> dietFoods = dietFoodRepository.findDietFoodListByType(type);
        if (dietFoods.isEmpty()) {
            throw new EntityNotFoundException("Can't find DietFood by type");
        }
        return dietFoods.stream()
                .map(this::mapToDietFoodDTO)
                .collect(Collectors.toList());
    }

    /**
     * Id 리스트들로 DietFood DTO 리스트 조회
     * @param idList (List<Long>)
     * @return findDietFoods (List<DietFoodDTO>)
     */
    @Override
    public List<DietFoodDTO> findDietFoodsByIdList(List<Long> idList) {
        return idList.stream()
                .map(this::findDietFood)
                .collect(Collectors.toList());
    }

    /**
     * DietFoodDTO -> DietFood 엔티티로 전환
     * @param DTO (DietFoodDTO)
     * @return DietFood
     */
    @Override
    public DietFood convertToEntity(DietFoodDTO DTO) {
        DietFood dietFood = findOne(DTO.getId());
        if (dietFood == null) {
            throw new RuntimeException("DietFood not found with id: " + DTO.getId());
        }
        return dietFood;
    }

    /**
     * 이름으로 음식 찾기
     * @param name (String)
     * @return DietFood
     */
    @Override
    public DietFood findDietFoodByName(String name) {
        return dietFoodRepository.findDietFoodByName(name);
    }

    /**
     * 음식 이름으로 DB에 존재 여부
     * @param name (String)
     * @return Boolean
     */
    @Override
    public boolean checkExistByName(String name) {
        return dietFoodRepository.findDietFoodByName(name) != null;
    }

    /**
     * 음식 id로 음식 이름 반환(한국어, 영어)
     */
    public FoodNamesDTO findNamesByFoodId(Long foodId) {
        return dietFoodRepository.findNameByDietFoodId(foodId)
            .orElseThrow(() -> new NoSuchElementException("음식(id=" + foodId + ")을 찾을 수 없습니다."));
    }


    /**
     * DietFood-> DietFoodDTO
     * @param dietFood (DietFood)
     * @return dietFoodDTO (DietFoodDTO)
     */
    private DietFoodDTO mapToDietFoodDTO(DietFood dietFood) {

        return modelMapper.map(dietFood, DietFoodDTO.class);
        }
}
