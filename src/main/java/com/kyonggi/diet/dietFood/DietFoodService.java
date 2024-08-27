package com.kyonggi.diet.dietFood;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietFoodService {

    private final DietFoodRepository dietFoodRepository;

    @Transactional
    public void save(DietFood dietFood) {
        dietFoodRepository.save(dietFood);
    }

    public DietFood findOne(Long id) {
        return dietFoodRepository.findOne(id);
    }

    public List<DietFood> findAll() {
        return dietFoodRepository.findAll();
    }

    public List<DietFood> findRice() { return dietFoodRepository.findRice();}

    public List<DietFood> findSide() { return dietFoodRepository.findSide();}

    public List<DietFood> findSoup() { return dietFoodRepository.findSoup();}

    public List<DietFood> findDesert() { return dietFoodRepository.findDesert();}

    public List<DietFood> findDietFoodsByIdList(List<Long> IdList) {
        List<DietFood> findDietFoods = new ArrayList<>();
        for (Long findDietFoodId : IdList) {
            findDietFoods.add(findOne(findDietFoodId));
        }
        return findDietFoods;
    }
}
