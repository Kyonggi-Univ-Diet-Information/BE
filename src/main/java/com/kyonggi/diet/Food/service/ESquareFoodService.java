package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.ESquareFoodDTO;
import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.review.repository.ESquareFoodReviewRepository;
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
    private final ESquareFoodReviewRepository esquareFoodReviewRepository;

    public ESquareFoodService(ModelMapper modelMapper, TranslationService translationService,
                              ESquareFoodRepository esquareFoodRepository,
                              ESquareFoodReviewRepository esquareFoodReviewRepository) {
        super(modelMapper);
        this.translationService = translationService;
        this.esquareFoodRepository = esquareFoodRepository;
        this.esquareFoodReviewRepository = esquareFoodReviewRepository;
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
        ESquareFoodDTO dto = mapToDto(food, ESquareFoodDTO.class);
        dto.setAverageRating(esquareFoodReviewRepository.findAverageRatingByESquareFoodId(id));
        dto.setReviewCount((long) esquareFoodReviewRepository.getESquareReviewCount(id));

        return dto;
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

        Map<Long, Object[]> statsByFoodId = esquareFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> row));

        return all.stream()
                .map(food -> toDtoWithStats(food, statsByFoodId))
                .collect(Collectors.toList());
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
    public Map<ESquareCategory, List<ESquareFoodDTO>> findFoodByCategory(FoodSortType sort) {
        List<ESquareFood> foods = esquareFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("이스퀘어 음식 목록이 비어있습니다.");
        }

        Map<Long, Object[]> statsByFoodId = esquareFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                       .collect(Collectors.toMap(row -> (Long) row[0], row -> row));

        Map<ESquareCategory, List<ESquareFoodDTO>> mappingFoods = foods.stream()
                .collect(Collectors.groupingBy(
                        ESquareFood::getCategory,
                        Collectors.mapping(food -> toDtoWithStats(food, statsByFoodId), Collectors.toList())
                ));


        return mappingFoods.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sortDtoList(entry.getValue(), sort),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private ESquareFoodDTO toDtoWithStats(ESquareFood food, Map<Long, Object[]> statsByFoodId) {
            ESquareFoodDTO dto = super.mapToDto(food, ESquareFoodDTO.class);
            Object[] stats = statsByFoodId.get(food.getId());
            if (stats != null) {
                dto.setAverageRating((Double) stats[1]);
                dto.setReviewCount((Long) stats[2]);
            }
            return dto;
        }

        private List<ESquareFoodDTO> sortDtoList(List<ESquareFoodDTO> list, FoodSortType sort) {
            if (sort == null) return list;
            Comparator<ESquareFoodDTO> comparator = switch (sort) {
                case RATING -> Comparator.comparing(ESquareFoodDTO::getAverageRating,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                case NAME -> Comparator.comparing(ESquareFoodDTO::getName);
                case REVIEW_COUNT -> Comparator.comparing(ESquareFoodDTO::getReviewCount,
                        Comparator.nullsLast(Comparator.reverseOrder()));
            };
            return list.stream().sorted(comparator).collect(Collectors.toList());
        }
}
