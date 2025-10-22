package com.kyonggi.diet.Food.service;

import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공통 Food 서비스 추상 클래스
 * DietFoodService, KyongsulFoodService
 */
@Transactional(readOnly = true)
public abstract class AbstractFoodService<E, D> {

    protected final ModelMapper modelMapper;

    protected AbstractFoodService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * 저장 로직 (공통)
     */
    @Transactional
    public abstract E save(D dto);

    /**
     * ID로 조회 (공통)
     */
    public abstract D findById(Long id);

    /**
     * 전체 조회 (공통)
     */
    public abstract List<D> findAll();

    /**
     * 이름으로 존재 여부 (공통)
     */
    public abstract boolean existsByName(String name);

    /**
     * ID로 음식 이름 DTO 조회
     */
    public abstract FoodNamesDTO findNamesByFoodId(Long foodId);

    /**
     * 공통 매핑 함수
     */
    protected D mapToDto(E entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    protected List<D> mapToDtoList(List<E> entities, Class<D> dtoClass) {
        return entities.stream()
                .map(e -> mapToDto(e, dtoClass))
                .collect(Collectors.toList());
    }

}
