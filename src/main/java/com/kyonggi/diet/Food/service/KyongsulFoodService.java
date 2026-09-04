package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.KyongsulSetFoodDTO;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.Food.DTO.KyongsulFoodDTO;
import com.kyonggi.diet.Food.domain.KyongsulSetFood;
import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.Food.repository.KyongsulSetFoodRepository;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.review.repository.KyongsulFoodReviewRepository;
import com.kyonggi.diet.translation.service.TranslationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@Transactional(readOnly = true)
public class KyongsulFoodService extends AbstractFoodService<KyongsulFood, KyongsulFoodDTO> {

    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final KyongsulSetFoodRepository kyongsulSetFoodRepository;
    private final KyongsulFoodReviewRepository kyongsulFoodReviewRepository;
    private final TranslationService translationService;

    public KyongsulFoodService(ModelMapper modelMapper,
                               KyongsulFoodRepository kyongsulFoodRepository,
                               KyongsulSetFoodRepository kyongsulSetFoodRepository,
                               KyongsulFoodReviewRepository kyongsulFoodReviewRepository,
                               TranslationService translationService) {
        super(modelMapper);
        this.kyongsulFoodRepository = kyongsulFoodRepository;
        this.kyongsulSetFoodRepository = kyongsulSetFoodRepository;
        this.kyongsulFoodReviewRepository = kyongsulFoodReviewRepository;
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
                .cuisine(kyongsulFoodDTO.getCuisine())
                .foodType(kyongsulFoodDTO.getFoodType())
                .detailedMenu(kyongsulFoodDTO.getDetailedMenu())
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
        KyongsulFoodDTO dto = mapToDto(food, KyongsulFoodDTO.class);
        dto.setAverageRating(kyongsulFoodReviewRepository.findAverageRatingByKyongsulFoodId(id));
        dto.setReviewCount((long) kyongsulFoodReviewRepository.getKyongsulReviewCount(id));
        return dto;
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
        Map<Long, Object[]> statsByFoodId = kyongsulFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> row));
        return all.stream()
                .map(food -> toDtoWithStats(food, statsByFoodId))
                .collect(Collectors.toList());
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
        Map<Long, Object[]> statsByFoodId = kyongsulFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> row));

        return foods.stream()
                .map(food -> toDtoWithStats(food, statsByFoodId))
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
     * 세트, 콤보 테이블 내 음식 한개 조회
     */
    public KyongsulSetFoodDTO findOneSetDTO(Long id) {
        return mapToDto(kyongsulSetFoodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("음식(id=" + id + ")을 찾을 수 없습니다.")));
    }

    /**
     * 단품 음식 id로 하위(세트, 콤보) 존재하면 조회
     */
    public List<KyongsulSetFoodDTO> findByBaseFood(Long baseFoodId) {
        List<KyongsulSetFood> all = kyongsulSetFoodRepository.findAllByBaseFoodId(baseFoodId);
        if (all.isEmpty()) {
            throw new NotFoundException("해당 단품에는 하위 목록이 없습니다.");
        }
        return mapToListDto(all);
    }

    private KyongsulSetFoodDTO mapToDto(KyongsulSetFood entity) {
        KyongsulSetFoodDTO dto = modelMapper.map(entity, KyongsulSetFoodDTO.class);

        dto.setBaseFoodId(
            entity.getBaseFood() != null ? entity.getBaseFood().getId() : null
        );

        return dto;
    }

    private List<KyongsulSetFoodDTO> mapToListDto(List<KyongsulSetFood> entities) {
        return entities.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList());
    }

    /**
     * 단일 기준(요리방식, 음식종류, 세부메뉴 등) 그룹핑 공통 메서드
     */
    private <K> Map<K, List<KyongsulFoodDTO>> groupFoodsBy(Function<KyongsulFood, K> classifier) {
        List<KyongsulFood> foods = kyongsulFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("경슐랭 음식 목록이 비어있습니다.");
        }

        return foods.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        LinkedHashMap::new,
                        Collectors.mapping(food -> super.mapToDto(food, KyongsulFoodDTO.class), Collectors.toList())
                ));
    }

    /**
     * 요리 방식별 조회
     */
    public Map<Cuisine, List<KyongsulFoodDTO>> findFoodByCuisine() {
        return groupFoodsBy(KyongsulFood::getCuisine);
    }

    /**
     * 음식 종류별 조회
     */
    public Map<FoodType, List<KyongsulFoodDTO>> findFoodByFoodType() {
        return groupFoodsBy(KyongsulFood::getFoodType);
    }

    /**
     * 세부 메뉴별 조회
     */
    public Map<DetailedMenu, List<KyongsulFoodDTO>> findFoodByDetailedMenu() {
        return groupFoodsBy(KyongsulFood::getDetailedMenu);
    }


    /**
     * 경슐랭 카테고리별 음식 출력
     */
    public Map<SubRestaurant, Map<KyongsulCategory, List<KyongsulFoodDTO>>>
        findFoodByCategory(FoodSortType sort) {
        List<KyongsulFood> foods = kyongsulFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("경슐랭 음식 목록이 비어있습니다.");
        }

        Map<Long, Object[]> statsByFoodId = kyongsulFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                        .collect(Collectors.toMap(row -> (Long) row[0], row -> row));

        Map<SubRestaurant, List<KyongsulFood>> bySubRestaurant =
                foods.stream().collect(Collectors.groupingBy(KyongsulFood::getSubRestaurant));

        Map<SubRestaurant, Map<KyongsulCategory, List<KyongsulFoodDTO>>> result = new LinkedHashMap<>();

        for (SubRestaurant sub : SubRestaurant.values()) {
            List<KyongsulFood> restaurantFoods = bySubRestaurant.getOrDefault(sub, List.of());

            Map<KyongsulCategory, List<KyongsulFoodDTO>> byCategory = restaurantFoods.stream()
                    .collect(Collectors.groupingBy(
                            KyongsulFood::getCategory,
                            Collectors.mapping(food -> toDtoWithStats(food, statsByFoodId), Collectors.toList())
                    ));

            Map<KyongsulCategory, List<KyongsulFoodDTO>> filteredCategoryMap = byCategory.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> sortDtoList(entry.getValue(), sort),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

            if (!filteredCategoryMap.isEmpty()) {
                result.put(sub, filteredCategoryMap);
            }
        }
        return result;
    }

    private KyongsulFoodDTO toDtoWithStats(KyongsulFood food, Map<Long, Object[]> statsByFoodId) {
           KyongsulFoodDTO dto = super.mapToDto(food, KyongsulFoodDTO.class);
           Object[] stats = statsByFoodId.get(food.getId());
           if (stats != null) {
               dto.setAverageRating((Double) stats[1]);
               dto.setReviewCount((Long) stats[2]);
           }
           return dto;
       }

       /** sort가 null(파라미터 없음/인식 불가)이면 기존 순서 그대로 반환 */
       private List<KyongsulFoodDTO> sortDtoList(List<KyongsulFoodDTO> list, FoodSortType sort) {
           if (sort == null) return list;
           Comparator<KyongsulFoodDTO> comparator = switch (sort) {
               case RATING -> Comparator.comparing(KyongsulFoodDTO::getAverageRating,
                               Comparator.nullsLast(Comparator.reverseOrder()))
                       .thenComparing(KyongsulFoodDTO::getReviewCount,
                               Comparator.nullsLast(Comparator.reverseOrder()));
               case NAME -> Comparator.comparing(KyongsulFoodDTO::getName);
               case REVIEW_COUNT -> Comparator.comparing(KyongsulFoodDTO::getReviewCount,
                               Comparator.nullsLast(Comparator.reverseOrder()))
                       .thenComparing(KyongsulFoodDTO::getAverageRating,
                               Comparator.nullsLast(Comparator.reverseOrder()));
           };
           return list.stream().sorted(comparator).collect(Collectors.toList());
       }
}
