package com.kyonggi.diet.controllerDocs;

import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface CSVControllerDocs {

    @Operation(summary = "CSV 파일 리더", description = "경로를 요청값으로 하여, 해당 경로에 있는 CSV 파일의 정보를 읽는 API")
    public void readDiet(@RequestBody String path) throws CsvValidationException, IOException;
}
