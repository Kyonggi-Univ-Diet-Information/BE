package com.kyonggi.diet.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "상태 API", description = "상태 확인하는 API 입니다.")
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<String> homeCheck() {
        return new ResponseEntity<>("Home Screen", HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
