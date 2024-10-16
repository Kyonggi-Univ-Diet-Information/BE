package com.kyonggi.diet.dietFile;

import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-csv")
public class CSVController {

    private final CSVService csvService;

    @PostMapping("/diet")
    public void readDiet(@RequestBody String path) throws CsvValidationException, IOException {
        if(path == null)
            return;
        csvService.readAndSave(path);
    }

}
