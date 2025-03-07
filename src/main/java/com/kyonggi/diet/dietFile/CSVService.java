package com.kyonggi.diet.dietFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.kyonggi.diet.diet.DietDTO;
import com.kyonggi.diet.dietContent.DTO.DietContentDTO;
import com.kyonggi.diet.dietContent.DietTime;
import com.kyonggi.diet.dietContent.service.DietContentService;
import com.kyonggi.diet.dietFood.DietFoodDTO;
import com.kyonggi.diet.dietFood.service.DietFoodService;
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

                    StringTokenizer st = new StringTokenizer(str, "&"); //&로 구분된 식단을 각 메뉴로 받아냄
                    while (st.hasMoreTokens()) {
                        foods.add(st.nextToken());
                    }

                    for (String food : foods) { //식단의 각 음식에 대해서 db에 저장 (음식이 저장되어 있어야, 식단 설정 가능)
                        DietFoodDTO dietFoodDTO = DietFoodDTO.builder()
                                .name(food).build();
                        dietFoodService.save(dietFoodDTO);

                        dietFoodDTO.setId(dietFoodService.findDietFoodByName(food).getId()); //저장된 음식을 기반으로, DietFoodDTO 설정

                        dietDTOS.add(DietDTO.builder()  //설정된 dietFoodDTO 를 기반으로, dietDTOS 설정
                                .dietFoodDTO(dietFoodDTO).build());
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
