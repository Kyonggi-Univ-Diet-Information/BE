package com.kyonggi.diet.kyongsul;

import com.amazonaws.services.kms.model.NotFoundException;
import com.kyonggi.diet.review.DTO.FoodNamesDTO;
import com.kyonggi.diet.translation.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KyongsulFoodService {

    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final ModelMapper modelMapper;
    private final TranslationService translationService;

    @Transactional
    public void save(KyongsulFoodDTO kyongsulFoodDTO) {
        KyongsulFood kyongsulFood = KyongsulFood.builder()
                .name(kyongsulFoodDTO.getName())
                .nameEn(translationService.translateToEnglish(kyongsulFoodDTO.getName()))
                .subRestaurant(kyongsulFoodDTO.getSubRestaurant()).build();

        kyongsulFoodRepository.save(kyongsulFood);
    }

    /**
     * ID 값으로 해당 경슐랭 음식 DTO 찾기
     * @param id (Long)
     * @return KyongsulFoodDTO
     */
    public KyongsulFoodDTO findById(Long id) {
        KyongsulFood food = kyongsulFoodRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("해당 ID값의 경슐 음식 찾을 수 없습니다."));
        return modelMapper.map(food, KyongsulFoodDTO.class);
    }

    /**
     * 경슐랭 음식 DTO 전체 찾기
     * @return List<KyongsulFoodDTO>
     */
    public List<KyongsulFoodDTO> findAll() {
        List<KyongsulFood> all = kyongsulFoodRepository.findAll();
        if (all.isEmpty()) {
            throw new NotFoundException("경슐랭 음식 전체 찾기 실패(비어있음)");
        }
        return all.stream()
                .map(this::mapToKyongsulFoodDTO)
                .collect(Collectors.toList());
    }

    /**
     * 경슐랭 서브 식당 이름으로 경슐랭 음식 DTO 전체 찾기
     * @param subRestaurant (SubRestaurant)
     * @return List<KyongsulFoodDTO>
     */
    public List<KyongsulFoodDTO> findBySubRestaurant(SubRestaurant subRestaurant) {
        List<KyongsulFood> foods = kyongsulFoodRepository.findBySubRestaurant(subRestaurant);
        if (foods.isEmpty()) {
            throw new NotFoundException("해당 서브 식당으로 경슐랭 음식 찾기 실패 (비어있음)");
        }
        return foods.stream().map(this::mapToKyongsulFoodDTO).collect(Collectors.toList());
    }

    /**
     * 음식 id로 음식 이름 반환
     */
    public FoodNamesDTO findNamesByFoodId(Long foodId) {
        return kyongsulFoodRepository.findNameByKyongsulFoodId(foodId)
            .orElseThrow(() -> new NoSuchElementException("음식(id=" + foodId + ")을 찾을 수 없습니다."));
    }


    /**
     * KyongsulFood -> KyongsulFoodDTO 변환기
     * @param kyongsulFood (KyongsulFood)
     * @return KyongsulFoodDTO
     */
    private KyongsulFoodDTO mapToKyongsulFoodDTO(KyongsulFood kyongsulFood) {
        return modelMapper.map(kyongsulFood, KyongsulFoodDTO.class);
    }
}
