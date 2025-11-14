package com.kyonggi.diet.dietFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.kyonggi.diet.Food.domain.*;
import com.kyonggi.diet.Food.eumer.*;
import com.kyonggi.diet.Food.repository.ESquareFoodRepository;
import com.kyonggi.diet.Food.repository.SallyBoxFoodRepository;
import com.kyonggi.diet.Food.service.DietFoodService;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietTime;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.Food.DTO.DietFoodDTO;
import com.kyonggi.diet.Food.repository.KyongsulFoodRepository;
import com.kyonggi.diet.translation.service.TranslationService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class CSVService {

    @Value("${cloud.aws.s3.bucketName.diet}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucketName.kyongsul}")
    private String bucketNameKyongsul;

    @Value("${cloud.aws.s3.bucketName.eSquare}")
    private String bucketNameESquare;

    @Value("${cloud.aws.s3.bucketName.sallyBox}")
    private String bucketNameSallyBox;

    private final AmazonS3 amazonS3;
    private final DietContentService dietContentService;
    private final DietFoodService dietFoodService;
    private final KyongsulFoodRepository kyongsulFoodRepository;
    private final TranslationService translationService;
    private final ESquareFoodRepository esquareFoodRepository;
    private final SallyBoxFoodRepository sallyBoxFoodRepository;

    //-------------------------------기숙사------------------------------------------
    @Transactional
    public void readAndSave(String key) throws IOException, CsvValidationException {

        S3Object s3Object = amazonS3.getObject(bucketName, key);
        InputStream inputStream = s3Object.getObjectContent();

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] nextLine;

            reader.readNext(); //csv 파일의 첫 줄 건넘 띔 (헤더 제거 목적)
            while ((nextLine = reader.readNext()) != null) {

                for (int j = 1; j <= 3; j++) {

                    if (nextLine[j].contains("미운영") || nextLine[j].isEmpty()) //식당 미운영 시 건너 띔
                        continue;

                    List<DietDTO> dietDTOS = new ArrayList<>();
                    List<String> foods = new ArrayList<>();
                    String str = nextLine[j];

                    StringTokenizer st = new StringTokenizer(str, "&/"); //&로 구분된 식단을 각 메뉴로 받아냄
                    while (st.hasMoreTokens()) {
                        foods.add(st.nextToken());
                    }

                    for (String food : foods) {
                        DietFood existing = dietFoodService.findDietFoodByName(food);

                        String nameEn;
                        if (existing != null) {
                            nameEn = (existing.getNameEn() != null)
                                    ? existing.getNameEn()
                                    : translationService.translateToEnglish(food); // nameEn이 NULL일 때만 번역
                        } else {
                            nameEn = translationService.translateToEnglish(food);
                        }

                        DietFoodDTO dietFoodDTO = DietFoodDTO.builder()
                                .name(food)
                                .nameEn(nameEn)
                                .build();

                        DietFood savedEntity = dietFoodService.save(dietFoodDTO);

                        DietFoodDTO savedDTO = DietFoodDTO.builder()
                                .id(savedEntity.getId())
                                .name(savedEntity.getName())
                                .nameEn(savedEntity.getNameEn())
                                .type(savedEntity.getDietFoodType())
                                .build();

                        dietDTOS.add(DietDTO.builder()
                                .dietFoodDTO(savedDTO)
                                .build());
                    }


                    DietContentDTO dietContentDTO = DietContentDTO
                            .builder()
                            .date(nextLine[0].substring(0, nextLine[0].length() - 4))
                            .time(sortDietTime(j))
                            .contents(dietDTOS).build();
                    dietContentService.save(dietContentDTO);
                }


            }
            reader.close();
        }
    }

    public DietTime sortDietTime(int j) {
        return switch (j) {
            case 1 -> DietTime.BREAKFAST;
            case 2 -> DietTime.LUNCH;
            case 3 -> DietTime.DINNER;
            default -> null;
        };
    }

    // ---------------- 공통 CSV 처리 메서드 ----------------
        private <T> void readAndSaveCSV(
                String bucketName,
                String key,
                Function<String[], T> entityMapper,
                JpaRepository<T, Long> repository
        ) throws IOException, CsvValidationException {

            S3Object s3Object = amazonS3.getObject(bucketName, key);
            try (CSVReader reader = new CSVReader(
                    new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {

                reader.readNext(); // 헤더 스킵
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length < 3 || nextLine[0].isEmpty()) continue;
                    T entity = entityMapper.apply(nextLine);
                    if (entity != null) repository.save(entity);
                }
            }
        }

        // ---------------- 경슐랭 ----------------
        @Transactional
        public void readKyongsulCSVFile(String key) throws IOException, CsvValidationException {
            readAndSaveCSV(bucketNameKyongsul, key, this::mapKyongsul, kyongsulFoodRepository);
        }

        private KyongsulFood mapKyongsul(String[] nextLine) {
            if (nextLine.length < 7 || nextLine[0].isEmpty()) return null;

            String restaurantStr = nextLine[0];
            String name = nextLine[1].trim();
            Long price = parsePrice(nextLine[2]);
            String nameEn = nextLine[3] == null || nextLine[3].isBlank()
                    ? translationService.translateToEnglish(name)
                    : nextLine[3].trim();

            Cuisine cuisine = Cuisine.valueOf(nextLine[4].trim().toUpperCase());
            FoodType foodType = FoodType.valueOf(nextLine[5].trim().toUpperCase());
            DetailedMenu detailedMenu = DetailedMenu.valueOf(nextLine[6].trim().toUpperCase());
            SubRestaurant subRestaurant = SubRestaurant.valueOf(restaurantStr);

            Optional<KyongsulFood> exist = kyongsulFoodRepository.findByName(name);
            if (exist.isPresent()) {
                if (exist.get().getCuisine() == null)
                    exist.get().updateCategory(cuisine, foodType, detailedMenu);
                return null;
            }

            return KyongsulFood.builder()
                    .subRestaurant(subRestaurant)
                    .name(name)
                    .nameEn(nameEn)
                    .price(price)
                    .cuisine(cuisine)
                    .foodType(foodType)
                    .detailedMenu(detailedMenu)
                    .build();
        }

        // ---------------- 이스퀘어 ----------------
        @Transactional
        public void readESquareCSVFile(String key) throws IOException, CsvValidationException {
            readAndSaveCSV(bucketNameESquare, key, this::mapESquare, esquareFoodRepository);
        }

        private ESquareFood mapESquare(String[] nextLine) {
            if (nextLine.length < 6 || nextLine[0].isEmpty()) return null;

            String name = nextLine[0].trim();
            Long price = parsePrice(nextLine[1]);
            String nameEn = nextLine[2] == null || nextLine[2].isBlank()
                    ? translationService.translateToEnglish(name)
                    : nextLine[2].trim();

            Cuisine cuisine = Cuisine.valueOf(nextLine[3].trim().toUpperCase());
            FoodType foodType = FoodType.valueOf(nextLine[4].trim().toUpperCase());
            DetailedMenu detailedMenu = DetailedMenu.valueOf(nextLine[5].trim().toUpperCase());

            Optional<ESquareFood> exist = esquareFoodRepository.findByName(name);
            if (exist.isPresent()) {
                if (exist.get().getCuisine() == null)
                    exist.get().updateCategory(cuisine, foodType, detailedMenu);
                return null;
            }

            return ESquareFood.builder()
                    .name(name)
                    .nameEn(nameEn)
                    .price(price)
                    .cuisine(cuisine)
                    .foodType(foodType)
                    .detailedMenu(detailedMenu)
                    .build();
        }

        // ---------------- 샐리박스 ----------------
        @Transactional
        public void readSallyBoxCSVFile(String key) throws IOException, CsvValidationException {
            readAndSaveCSV(bucketNameSallyBox, key, this::mapSallyBox, sallyBoxFoodRepository);
        }

        private SallyBoxFood mapSallyBox(String[] nextLine) {
            if (nextLine.length < 6 || nextLine[0].isEmpty()) return null;

            String name = nextLine[0].trim();
            Long price = parsePrice(nextLine[1]);
            String nameEn = nextLine[2] == null || nextLine[2].isBlank()
                    ? translationService.translateToEnglish(name)
                    : nextLine[2].trim();

            Cuisine cuisine = Cuisine.valueOf(nextLine[3].trim().toUpperCase());
            FoodType foodType = FoodType.valueOf(nextLine[4].trim().toUpperCase());
            DetailedMenu detailedMenu = DetailedMenu.valueOf(nextLine[5].trim().toUpperCase());

            Optional<SallyBoxFood> exist = sallyBoxFoodRepository.findByName(name);
            if (exist.isPresent()) {
                if (exist.get().getCuisine() == null)
                    exist.get().updateCategory(cuisine, foodType, detailedMenu);
                return null;
            }

            return SallyBoxFood.builder()
                    .name(name)
                    .nameEn(nameEn)
                    .price(price)
                    .cuisine(cuisine)
                    .foodType(foodType)
                    .detailedMenu(detailedMenu)
                    .build();
        }

    private Long parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return 0L;
        String digits = priceStr.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0L;
        return Long.parseLong(digits);
    }
}
