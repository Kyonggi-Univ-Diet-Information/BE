package com.kyonggi.diet.dietFood.service.Impl;

import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.DietFoodRepository;
import com.kyonggi.diet.dietFood.DietFoodType;
import com.kyonggi.diet.dietFood.service.DietFoodService;
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
     * @param dietFood (DietFood)
     */
    @Transactional
    @Override
    public void save(DietFood dietFood) {
        dietFoodRepository.save(dietFood);
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
        log.info("아이디: {}",DTO.getId());
        DietFood dietFood = findOne(DTO.getId());
        if (dietFood == null) {
            throw new RuntimeException("DietFood not found with id: " + DTO.getId());
        }
        return dietFood;
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
