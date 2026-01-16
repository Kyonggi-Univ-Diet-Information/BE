package com.kyonggi.diet.auth.google;

import com.kyonggi.diet.auth.google.dto.GoogleLoginRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "구글 인증 API", description = "구글 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google-login")
    public AuthResponse googleLogin(
            @Valid @RequestBody GoogleLoginRequest code) {

        return googleAuthService.login(code.getCode());
    }

    @GetMapping("/oauth2/google")
    @Operation(summary = "[백엔드 테스트용] 구글 인가 코드 수신", description = "구글로부터 인가 코드를 수신합니다.")
    public String receiveCode(@RequestParam String code) {
        log.info("Received google authorization code: {}", code);
        return "CODE = " + code;
    }

    @DeleteMapping("/google-revoke")
    @Operation(summary = "구글 회원탈퇴", description = "구글 회원 탈퇴합니다.")
    public ResponseEntity<String> revokeToken(@RequestHeader("Authorization") String token) {
        googleAuthService.revokeToken(token.substring("Bearer ".length()));
        return ResponseEntity.ok("Google account revoked successfully.");
    }
}