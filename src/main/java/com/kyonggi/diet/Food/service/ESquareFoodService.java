package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.ESquareFoodDTO;
import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.eumer.Cuisine;
import com.kyonggi.diet.Food.eumer.DetailedMenu;
import com.kyonggi.diet.Food.eumer.ESquareCategory;
import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.translation.service.TranslationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ESquareFoodService extends AbstractFoodService<ESquareFood, ESquareFoodDTO> {

    private final TranslationService translationService;
    private final ESquareFoodRepository esquareFoodRepository;

    public ESquareFoodService(ModelMapper modelMapper, TranslationService translationService,
                              ESquareFoodRepository esquareFoodRepository) {
        super(modelMapper);
        this.translationService = translationService;
        this.esquareFoodRepository = esquareFoodRepository;
    }

    /**
     * 저장 메서드
     */
    @Transactional
    @Override
    public ESquareFood save(ESquareFoodDTO DTO) {
        ESquareFood food = ESquareFood.builder()
                .name(DTO.getName())
                .nameEn(translationService.translateToEnglish(DTO.getName()))
                .price(DTO.getPrice())
                .cuisine(DTO.getCuisine())
                .foodType(DTO.getFoodType())
                .detailedMenu(DTO.getDetailedMenu())
                .build();

        return esquareFoodRepository.save(food);
    }

    /**
     * ID 값으로 음식 DTO 찾기
     *
     * @param id (Long)
     * @return ESquareFoodDTO
     */
    @Override
    public ESquareFoodDTO findById(Long id) {
        ESquareFood food = esquareFoodRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 ID값의 이스퀘어 음식 찾을 수 없습니다."));
        return mapToDto(food, ESquareFoodDTO.class);
    }

    /**
     * 이스퀘어 음식 DTO 전체 찾기
     *
     * @return List<KyongsulFoodDTO>
     */
    @Override
    public List<ESquareFoodDTO> findAll() {
        List<ESquareFood> all = esquareFoodRepository.findAll();
        if (all.isEmpty()) {
            throw new NotFoundException("이스퀘어 음식 전체 찾기 실패(비어있음)");
        }
        return mapToDtoList(all, ESquareFoodDTO.class);
    }

    /**
     * 음식 id로 음식 이름 반환
     */
    @Override
    public FoodNamesDTO findNamesByFoodId(Long foodId) {
        return esquareFoodRepository.findNameByESquareFoodId(foodId)
                .orElseThrow(() -> new NoSuchElementException("음식(id=" + foodId + ")을 찾을 수 없습니다."));
    }

    /**
     * 음식 이름으로 DB에 존재 여부
     *
     * @param name (String)
     * @return Boolean
     */
    @Override
    public boolean existsByName(String name) {
        return esquareFoodRepository.findByName(name).isPresent();
    }

    /**
     * 요리 방식별 조회
     */
    public Map<Cuisine, List<ESquareFoodDTO>> findFoodByCuisine() {
        return groupFoodsBy(ESquareFood::getCuisine);
    }

    /**
     * 음식 종류별 조회
     */
    public Map<FoodType, List<ESquareFoodDTO>> findFoodByFoodType() {
        return groupFoodsBy(ESquareFood::getFoodType);
    }

    /**
     * 세부 메뉴별 조회
     */
    public Map<DetailedMenu, List<ESquareFoodDTO>> findFoodByDetailedMenu() {
        return groupFoodsBy(ESquareFood::getDetailedMenu);
    }

    /**
     *
     */
    private <K> Map<K, List<ESquareFoodDTO>> groupFoodsBy(Function<ESquareFood, K> classifier) {
        List<ESquareFood> foods = esquareFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("이스퀘어 음식 목록이 비어있습니다.");
        }

        return foods.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        LinkedHashMap::new, // 순서 유지
                        Collectors.mapping(food -> super.mapToDto(food, ESquareFoodDTO.class), Collectors.toList())
                ));
    }

    /**
     * 이스퀘어 카테고리별 음식 출력
     */
    public Map<ESquareCategory, List<ESquareFoodDTO>> findFoodByCategory() {
        List<ESquareFood> foods = esquareFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("이스퀘어 음식 목록이 비어있습니다.");
        }

        Map<ESquareCategory, List<ESquareFoodDTO>> mappingFoods = foods.stream()
                .collect(Collectors.groupingBy(
                        ESquareFood::getCategory,
                        Collectors.mapping(food -> super.mapToDto(food, ESquareFoodDTO.class), Collectors.toList())
                ));


        return mappingFoods.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
