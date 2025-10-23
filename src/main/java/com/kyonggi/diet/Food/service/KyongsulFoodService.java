package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.ESquareFoodDTO;
import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.Food.DTO.KyongsulFoodDTO;
import com.kyonggi.diet.Food.eumer.ESquareCategory;
import com.kyonggi.diet.Food.eumer.KyongsulCategory;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.translation.service.TranslationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class KyongsulFoodService extends AbstractFoodService<KyongsulFood, KyongsulFoodDTO> {

    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final TranslationService translationService;

    public KyongsulFoodService(ModelMapper modelMapper,
                               KyongsulFoodRepository kyongsulFoodRepository,
                               TranslationService translationService) {
        super(modelMapper);
        this.kyongsulFoodRepository = kyongsulFoodRepository;
        this.translationService = translationService;
    }
    /**
     * 저장 메서드
     *
     * @param kyongsulFoodDTO (KyongsulFoodDTO)
     */
    @Transactional
    @Override
    public KyongsulFood save(KyongsulFoodDTO kyongsulFoodDTO) {
        KyongsulFood kyongsulFood = KyongsulFood.builder()
                .name(kyongsulFoodDTO.getName())
                .nameEn(translationService.translateToEnglish(kyongsulFoodDTO.getName()))
                .subRestaurant(kyongsulFoodDTO.getSubRestaurant())
                .build();

        return kyongsulFoodRepository.save(kyongsulFood);
    }

    /**
     * ID 값으로 해당 경슐랭 음식 DTO 찾기
     *
     * @param id (Long)
     * @return KyongsulFoodDTO
     */
    @Override
    public KyongsulFoodDTO findById(Long id) {
        KyongsulFood food = kyongsulFoodRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 ID값의 경슐 음식 찾을 수 없습니다."));
        return mapToDto(food, KyongsulFoodDTO.class);
    }

    /**
     * 경슐랭 음식 DTO 전체 찾기
     *
     * @return List<KyongsulFoodDTO>
     */
    @Override
    public List<KyongsulFoodDTO> findAll() {
        List<KyongsulFood> all = kyongsulFoodRepository.findAll();
        if (all.isEmpty()) {
            throw new NotFoundException("경슐랭 음식 전체 찾기 실패(비어있음)");
        }
        return mapToDtoList(all, KyongsulFoodDTO.class);
    }

    /**
     * 경슐랭 서브 식당 이름으로 경슐랭 음식 DTO 전체 찾기
     *
     * @param subRestaurant (SubRestaurant)
     * @return List<KyongsulFoodDTO>
     */
    public List<KyongsulFoodDTO> findBySubRestaurant(SubRestaurant subRestaurant) {
        List<KyongsulFood> foods = kyongsulFoodRepository.findBySubRestaurant(subRestaurant);
        if (foods.isEmpty()) {
            throw new NotFoundException("해당 서브 식당으로 경슐랭 음식 찾기 실패 (비어있음)");
        }
        return foods.stream()
                .map(food -> mapToDto(food, KyongsulFoodDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 음식 id로 음식 이름 반환
     */
    @Override
    public FoodNamesDTO findNamesByFoodId(Long foodId) {
        return kyongsulFoodRepository.findNameByKyongsulFoodId(foodId)
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
        return kyongsulFoodRepository.findByName(name).isPresent();
    }

    /**
     * 경슐랭 카테고리별 음식 출력
     */
    public Map<SubRestaurant, Map<KyongsulCategory, List<KyongsulFoodDTO>>> findFoodByCategory() {
        List<KyongsulFood> foods = kyongsulFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("경슐랭 음식 목록이 비어있습니다.");
        }

        Map<SubRestaurant, List<KyongsulFood>> bySubRestaurant =
                foods.stream().collect(Collectors.groupingBy(KyongsulFood::getSubRestaurant));

        Map<SubRestaurant, Map<KyongsulCategory, List<KyongsulFoodDTO>>> result = new LinkedHashMap<>();

        for (SubRestaurant sub : SubRestaurant.values()) {
            List<KyongsulFood> restaurantFoods = bySubRestaurant.getOrDefault(sub, List.of());

            Map<KyongsulCategory, List<KyongsulFoodDTO>> byCategory = restaurantFoods.stream()
                    .collect(Collectors.groupingBy(
                            KyongsulFood::getCategory,
                            Collectors.mapping(food -> super.mapToDto(food, KyongsulFoodDTO.class), Collectors.toList())
                    ));

            Map<KyongsulCategory, List<KyongsulFoodDTO>> filteredCategoryMap = byCategory.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

            if (!filteredCategoryMap.isEmpty()) {
                result.put(sub, filteredCategoryMap);
            }
        }
        return result;
    }
}
