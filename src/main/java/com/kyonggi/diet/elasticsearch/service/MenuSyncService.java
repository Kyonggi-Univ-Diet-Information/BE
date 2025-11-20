package com.kyonggi.diet.elasticsearch.service;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import com.kyonggi.diet.elasticsearch.repository.MenuSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuSyncService {

//    private final DietFoodRepository dietRepo;
    private final ESquareFoodRepository esquareRepo;
    private final KyongsulFoodRepository kyongsulRepo;
    private final SallyBoxFoodRepository sallyRepo;
    private final MenuSearchRepository esRepo;

    public void syncAll() {

//        sync(RestaurantType.DORMITORY, dietRepo.findAll());
        sync(RestaurantType.E_SQUARE, esquareRepo.findAll());
        sync(RestaurantType.KYONGSUL, kyongsulRepo.findAll());
        sync(RestaurantType.SALLY_BOX, sallyRepo.findAll());
    }

    private void sync(RestaurantType restaurantType, List<?> list) {

        for (Object obj : list) {

            MenuDocument doc = null;

//            if (obj instanceof DietFood f) {
//                doc = new MenuDocument(
//                        restaurantType + "_" + f.getId(),
//                        restaurantType,
//                        f.getName(),
//                        f.getId()
//                );
//            }

            if (obj instanceof ESquareFood f) {
                doc = new MenuDocument(
                        restaurantType + "_" + f.getId(),
                        restaurantType,
                        f.getName(),
                        f.getId()
                );
            }

            if (obj instanceof KyongsulFood f) {
                doc = new MenuDocument(
                        restaurantType + "_" + f.getId(),
                        restaurantType,
                        f.getName(),
                        f.getId()
                );
            }

            if (obj instanceof SallyBoxFood f) {
                doc = new MenuDocument(
                        restaurantType + "_" + f.getId(),
                        restaurantType,
                        f.getName(),
                        f.getId()
                );
            }

            if (doc != null) {
                esRepo.save(doc);
            }
        }
    }
}
