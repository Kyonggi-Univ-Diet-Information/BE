package com.kyonggi.diet.dietFile;

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

    @PostMapping("/diet")
    public void readDiet(@RequestBody String path) throws CsvValidationException, IOException {
        if(path == null)
            return;
        csvService.readAndSave(path);
    }

}
