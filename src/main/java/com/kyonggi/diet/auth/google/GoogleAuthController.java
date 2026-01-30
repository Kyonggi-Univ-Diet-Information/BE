package com.kyonggi.diet.auth.google;

import com.kyonggi.diet.auth.google.dto.GoogleLoginRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.io.AuthResponseWithRefresh;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "구글 인증 API", description = "구글 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final StringRedisTemplate redisTemplate;

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
    private static final long COOKIE_AGE_SECONDS = REFRESH_TOKEN_TTL.getSeconds();

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request) {
        AuthResponseWithRefresh tokens = googleAuthService.login(request.getCode());

        redisTemplate.opsForValue().set(
                "GOOGLE_REFRESH:" + tokens.getEmail(),
                tokens.getRefreshToken(),
                REFRESH_TOKEN_TTL
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(COOKIE_AGE_SECONDS)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new AuthResponse(tokens.getAccessToken(), tokens.getEmail()));
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
        try {
            String email = googleAuthService.revokeToken(token.substring("Bearer ".length()));
            redisTemplate.delete("GOOGLE_REFRESH:" + email);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(0)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body("Revoked");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}