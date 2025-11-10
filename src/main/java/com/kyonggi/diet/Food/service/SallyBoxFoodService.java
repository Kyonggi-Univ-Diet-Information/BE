package com.kyonggi.diet.Food.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.Food.DTO.DietFoodDTO;
import com.kyonggi.diet.Food.DTO.ESquareFoodDTO;
import com.kyonggi.diet.Food.DTO.SallyBoxFoodDTO;
import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.Food.eumer.DietFoodType;
import com.kyonggi.diet.Food.eumer.ESquareCategory;
import com.kyonggi.diet.Food.eumer.SallyBoxCategory;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.translation.service.TranslationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SallyBoxFoodService extends AbstractFoodService<SallyBoxFood, SallyBoxFoodDTO> {

    private final TranslationService translationService;
    private final SallyBoxFoodRepository sallyBoxFoodRepository;

    public SallyBoxFoodService(ModelMapper modelMapper, TranslationService translationService,
                               SallyBoxFoodRepository sallyBoxFoodRepository) {
        super(modelMapper);
        this.translationService = translationService;
        this.sallyBoxFoodRepository = sallyBoxFoodRepository;
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
                .category(DTO.getCategory())
                .categoryKorean(DTO.getCategory().getKoreanName())
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
        return mapToDto(food, SallyBoxFoodDTO.class);
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
        return mapToDtoList(all, SallyBoxFoodDTO.class);
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
     * 샐리박스 카테고리별 음식 출력
     */
    public Map<SallyBoxCategory, List<SallyBoxFoodDTO>> findFoodByCategory() {
        List<SallyBoxFood> foods = sallyBoxFoodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NotFoundException("쌜리박스 음식 목록이 비어있습니다.");
        }

        Map<SallyBoxCategory, List<SallyBoxFoodDTO>> mappingFoods = foods.stream()
                .collect(Collectors.groupingBy(
                        SallyBoxFood::getCategory,
                        Collectors.mapping(food -> super.mapToDto(food, SallyBoxFoodDTO.class), Collectors.toList())
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

    public List<SallyBoxFoodDTO> getFavoriteTop5Foods() {
        List<Object[]> results = sallyBoxFoodRepository.find5FoodFavorite(PageRequest.of(0, 5));

        return results.stream()
                .map(obj -> new SallyBoxFoodDTO(
                        (Long) obj[0],
                        (String) obj[1],
                        (String) obj[2],
                        (Long) obj[3],
                        (SallyBoxCategory) obj[4],
                        (String) obj[5],
                        (Long) obj[6]
                ))
                .collect(Collectors.toList());
    }
}
