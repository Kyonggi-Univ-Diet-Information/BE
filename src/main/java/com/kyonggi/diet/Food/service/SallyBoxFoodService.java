package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.SallyBoxFoodDTO;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.review.repository.SallyBoxFoodReviewRepository;
import com.kyonggi.diet.translation.service.TranslationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SallyBoxFoodService extends AbstractFoodService<SallyBoxFood, SallyBoxFoodDTO> {

    private final TranslationService translationService;
    private final SallyBoxFoodRepository sallyBoxFoodRepository;
    private final SallyBoxFoodReviewRepository sallyBoxFoodReviewRepository;

    public SallyBoxFoodService(ModelMapper modelMapper, TranslationService translationService,
                               SallyBoxFoodRepository sallyBoxFoodRepository,
                               SallyBoxFoodReviewRepository sallyBoxFoodReviewRepository) {
        super(modelMapper);
        this.translationService = translationService;
        this.sallyBoxFoodRepository = sallyBoxFoodRepository;
        this.sallyBoxFoodReviewRepository = sallyBoxFoodReviewRepository;
    }

    /**
     * 저장 메서드
     */
    @Transactional
    @Override
    public SallyBoxFood save(SallyBoxFoodDTO DTO) {
        SallyBoxFood food = SallyBoxFood.builder()
                .name(DTO.getName())
                .nameEn(translationService.translateToEnglish(DTO.getName()))
                .price(DTO.getPrice())
                .cuisine(DTO.getCuisine())
                .foodType(DTO.getFoodType())
                .detailedMenu(DTO.getDetailedMenu())
                .build();

        return sallyBoxFoodRepository.save(food);
    }

    /**
     * ID 값으로 음식 DTO 찾기
     *
     * @param id (Long)
     */
    @Override
    public SallyBoxFoodDTO findById(Long id) {
        SallyBoxFood food = sallyBoxFoodRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 ID값의 샐리박스 음식 찾을 수 없습니다."));
        SallyBoxFoodDTO dto = mapToDto(food, SallyBoxFoodDTO.class);
        dto.setAverageRating(sallyBoxFoodReviewRepository.findAverageRatingBySallyBoxFoodId(id));
        dto.setReviewCount((long) sallyBoxFoodReviewRepository.getSallyBoxReviewCount(id));
        return dto;
    }

    /**
     * 샐리박스 음식 DTO 전체 찾기
     */
    @Override
    public List<SallyBoxFoodDTO> findAll() {
        List<SallyBoxFood> all = sallyBoxFoodRepository.findAll();
        if (all.isEmpty()) {
            throw new NotFoundException("샐리박스 음식 전체 찾기 실패(비어있음)");
        }
        Map<Long, Object[]> statsByFoodId = sallyBoxFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
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
        return sallyBoxFoodRepository.findNameBySallyBoxFoodId(foodId)
                .orElseThrow(() -> new NoSuchElementException("음식(id=" + foodId + ")을 찾을 수 없습니다."));
    }

    /**
     * 음식 이름으로 DB에 존재 여부
     */
    @Override
    public boolean existsByName(String name) {
        return sallyBoxFoodRepository.findByName(name).isPresent();
    }

    /**
     * 요리 방식별 조회
     */
    public Map<Cuisine, List<SallyBoxFoodDTO>> findFoodByCuisine() {
        return groupFoodsBy(SallyBoxFood::getCuisine);
    }

    /**
     * 음식 종류별 조회
     */
    public Map<FoodType, List<SallyBoxFoodDTO>> findFoodByFoodType() {
        return groupFoodsBy(SallyBoxFood::getFoodType);
    }

    /**
     * 세부 메뉴별 조회
     */
    public Map<DetailedMenu, List<SallyBoxFoodDTO>> findFoodByDetailedMenu() {
        return groupFoodsBy(SallyBoxFood::getDetailedMenu);
    }

    private <K> Map<K, List<SallyBoxFoodDTO>> groupFoodsBy(Function<SallyBoxFood, K> classifier) {
        List<SallyBoxFood> foods = sallyBoxFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("샐리박스 음식 목록이 비어있습니다.");
        }

        return foods.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        LinkedHashMap::new, // 순서 유지
                        Collectors.mapping(food -> super.mapToDto(food, SallyBoxFoodDTO.class), Collectors.toList())
                ));
    }

    /**
     * 샐리박스 카테고리별 음식 출력
     */
    public Map<SallyBoxCategory, List<SallyBoxFoodDTO>> findFoodByCategory(FoodSortType sort) {
        List<SallyBoxFood> foods = sallyBoxFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("쌜리박스 음식 목록이 비어있습니다.");
        }

        Map<Long, Object[]> statsByFoodId = sallyBoxFoodReviewRepository.findRatingStatsGroupByFoodId().stream()
                        .collect(Collectors.toMap(row -> (Long) row[0], row -> row));

        Map<SallyBoxCategory, List<SallyBoxFoodDTO>> mappingFoods = foods.stream()
                .collect(Collectors.groupingBy(
                        SallyBoxFood::getCategory,
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

    private SallyBoxFoodDTO toDtoWithStats(SallyBoxFood food, Map<Long, Object[]> statsByFoodId) {
            SallyBoxFoodDTO dto = super.mapToDto(food, SallyBoxFoodDTO.class);
            Object[] stats = statsByFoodId.get(food.getId());
            if (stats != null) {
                dto.setAverageRating((Double) stats[1]);
                dto.setReviewCount((Long) stats[2]);
            }
            return dto;
        }

        private List<SallyBoxFoodDTO> sortDtoList(List<SallyBoxFoodDTO> list, FoodSortType sort) {
            if (sort == null) return list;
            Comparator<SallyBoxFoodDTO> comparator = switch (sort) {
                case RATING -> Comparator.comparing(SallyBoxFoodDTO::getAverageRating,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(SallyBoxFoodDTO::getReviewCount,
                                Comparator.nullsLast(Comparator.reverseOrder()));
                case NAME -> Comparator.comparing(SallyBoxFoodDTO::getName);
                case REVIEW_COUNT -> Comparator.comparing(SallyBoxFoodDTO::getReviewCount,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(SallyBoxFoodDTO::getAverageRating,
                                Comparator.nullsLast(Comparator.reverseOrder()));
            };
            return list.stream().sorted(comparator).collect(Collectors.toList());
        }
}
