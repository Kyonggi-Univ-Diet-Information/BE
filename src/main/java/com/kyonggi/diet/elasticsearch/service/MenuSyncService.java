package com.kyonggi.diet.elasticsearch.service;

import com.kyonggi.diet.Food.domain.ESquareFood;
import com.kyonggi.diet.Food.domain.KyongsulFood;
import com.kyonggi.diet.Food.domain.SallyBoxFood;
import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.Food.eumer.RestaurantType;
import com.kyonggi.diet.Food.eumer.SubRestaurant;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import com.kyonggi.diet.elasticsearch.repository.MenuSearchRepository;
import com.kyonggi.diet.review.service.ESquareFoodReviewService;
import com.kyonggi.diet.review.service.KyongsulFoodReviewService;
import com.kyonggi.diet.review.service.SallyBoxFoodReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuSyncService {

    private final ESquareFoodRepository esquareRepo;
    private final KyongsulFoodRepository kyongsulRepo;
    private final SallyBoxFoodRepository sallyRepo;
    private final ESquareFoodReviewService eSquareFoodReviewService;
    private final KyongsulFoodReviewService kyongsulReviewRepo;
    private final SallyBoxFoodReviewService sallyReviewRepo;
    private final MenuSearchRepository esRepo;

    public void syncAll() {
        sync(RestaurantType.E_SQUARE, esquareRepo.findAll());
        sync(RestaurantType.KYONGSUL, kyongsulRepo.findAll());
        sync(RestaurantType.SALLY_BOX, sallyRepo.findAll());
    }

    private void sync(RestaurantType type, List<?> list) {

        for (Object obj : list) {

            Long menuId = null;
            String menuName = null;
            String menuNameEn = null;
            FoodType category = null;
            SubRestaurant sub = null;
            long reviewCount = 0L;
            Double avg = 0.0;

            if (obj instanceof ESquareFood f) {
                menuId = f.getId();
                menuName = f.getName();
                menuNameEn = f.getNameEn();
                category = f.getFoodType();
                reviewCount = eSquareFoodReviewService.getReviewCount(f.getId());
                avg = eSquareFoodReviewService.getAverageRating(f.getId());
            }

            if (obj instanceof KyongsulFood f) {
                menuId = f.getId();
                menuName = f.getName();
                menuNameEn = f.getNameEn();
                category = f.getFoodType();
                sub = f.getSubRestaurant();
                reviewCount = kyongsulReviewRepo.getReviewCount(f.getId());
                avg = kyongsulReviewRepo.getAverageRating(f.getId());
            }

            if (obj instanceof SallyBoxFood f) {
                menuId = f.getId();
                menuName = f.getName();
                menuNameEn = f.getNameEn();
                category = f.getFoodType();
                reviewCount = sallyReviewRepo.getReviewCount(f.getId());
                avg = sallyReviewRepo.getAverageRating(f.getId());
            }

            if (menuId == null) continue;


            MenuDocument doc = MenuDocument.createAuto(
                    type,
                    sub,
                    menuName,
                    menuNameEn,
                    menuId,
                    reviewCount,
                    avg,
                    category
            );

            esRepo.save(doc);
        }
    }
}
