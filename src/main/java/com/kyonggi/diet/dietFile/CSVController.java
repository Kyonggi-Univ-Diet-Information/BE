package com.kyonggi.diet.dietFile;

import com.kyonggi.diet.controllerDocs.CSVControllerDocs;
import com.kyonggi.diet.translation.service.TranslationService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-csv")
@Tag(name = "CSV 파일 API", description = "CSV 파일을 읽기 위한 API 입니다.")
public class CSVController implements CSVControllerDocs {

    private final CSVService csvReader;
    private final TranslationService translationService;

    @PostMapping("/diet")
    public void readDiet(@RequestBody KeyDTO keyDTO) throws CsvValidationException, IOException {
        if(keyDTO.getKey() == null)
            return;
        csvReader.readAndSave(keyDTO.getKey());
    }

    @PostMapping("/kyongsul")
    public void readKyongsul(@RequestBody KeyDTO keyDTO) throws CsvValidationException, IOException {
        if(keyDTO.getKey() == null)
            return;
        csvReader.readerKyongsulExcelFile(keyDTO.getKey());
    }

    @PostMapping("/e-square")
    public void readESquare(@RequestBody KeyDTO keyDTO) throws CsvValidationException, IOException {
        if(keyDTO.getKey() == null)
            return;
        csvReader.readESquareCSVFile(keyDTO.getKey());
    }

}