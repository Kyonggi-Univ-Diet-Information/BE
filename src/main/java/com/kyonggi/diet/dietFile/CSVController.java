package com.kyonggi.diet.dietFile;

import com.kyonggi.diet.aws.S3FileRequest;
import com.kyonggi.diet.controllerDocs.CSVControllerDocs;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-csv")
@Tag(name = "CSV 파일 API", description = "CSV 파일을 읽기 위한 API 입니다.")
public class CSVController implements CSVControllerDocs {

    private final CSVService csvService;

    private final com.kyonggi.diet.aws.CSVServices csvReader;

    @PostMapping("/diet")
    public void readDiet(@RequestBody String path) throws CsvValidationException, IOException {
        if(path == null)
            return;
        csvService.readAndSave(path);
    }

    @PostMapping("/diet2")
    public void readDiet2(@RequestBody S3FileRequest s3FileRequest) throws CsvValidationException, IOException {
        if(s3FileRequest.getKey() == null)
            return;
        csvReader.readAndSave(s3FileRequest.getKey());
    }

}
