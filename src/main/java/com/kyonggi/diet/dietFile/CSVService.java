package com.kyonggi.diet.dietFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietTime;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.dietFood.DietFood;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
import com.kyonggi.diet.translation.service.TranslationService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Service
@Transactional
@RequiredArgsConstructor
public class CSVService {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    private final DietContentService dietContentService;
    private final DietFoodService dietFoodService;
    private final TranslationService translationService;

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
/*
* save 하고 다시 find 하는 부분 수정
* */
                    for (String food : foods) {
                        // 기존 엔티티 조회
                        DietFood existing = dietFoodService.findDietFoodByName(food);

                        String nameEn;
                        if (existing != null) {
                            // 이미 존재하면 그대로 사용
                            nameEn = (existing.getNameEn() != null)
                                    ? existing.getNameEn()
                                    : translationService.translateToEnglish(food); // nameEn이 NULL일 때만 번역
                        } else {
                            // 없으면 새로 번역
                            nameEn = translationService.translateToEnglish(food);
                        }

                        // DTO 생성
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




                    DietContentDTO dietContentDTO = DietContentDTO //최종적으로 Diet, DietContent db 저장 로직
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

    /**
     * 인덱스 별 식사 시간 구분
     *
     * @param j (int)
     * @return DietTime
     */
    public DietTime sortDietTime(int j) {
        return switch (j) {
            case 1 -> DietTime.BREAKFAST;
            case 2 -> DietTime.LUNCH;
            case 3 -> DietTime.DINNER;
            default -> null;
        };
    }

}
