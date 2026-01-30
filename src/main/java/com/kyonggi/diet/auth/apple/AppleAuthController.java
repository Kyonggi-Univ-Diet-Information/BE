package com.kyonggi.diet.auth.apple;

import com.kyonggi.diet.auth.apple.dto.AppleLoginRequest;
import com.kyonggi.diet.auth.apple.service.AppleLoginService;
import com.kyonggi.diet.auth.apple.service.AppleOAuthClient;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.io.AuthResponseWithRefresh;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(
        name = "애플 인증 API",
        description = "애플 소셜 회원 인증에 대한 API"
)
public class AppleAuthController {

    private final AppleLoginService appleLoginService;
    private final AppleOAuthClient appleOAuthClient;
    private final StringRedisTemplate redisTemplate;

    private static final Duration STATE_TTL = Duration.ofMinutes(5);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
    private static final long COOKIE_AGE_SECONDS = REFRESH_TOKEN_TTL.getSeconds();

    @GetMapping("/apple-form")
    public Map<String, String> getAppleLoginUrl() {
        // 1) state/nonce 생성
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        // 2) Redis에 state 기반 key로 nonce 저장
        redisTemplate.opsForValue().set("APPLE_STATE:" + state, nonce, STATE_TTL);

        // 3) Apple authorize URL 생성
        String url = appleOAuthClient.buildAuthorizeUrl(state, nonce);

        return Map.of("url", url);
    }

    @PostMapping("/apple-login")
    public ResponseEntity<AuthResponse> appleCallback(
            @RequestBody AppleLoginRequest request
    ) throws Exception {
        String code = request.getCode();
        String state = request.getState();
        AppleLoginRequest.AppleUser user = request.getUser(); // null 가능

        if (code == null || state == null) {
            throw new IllegalArgumentException("code or state is missing");
        }

        String storedNonce =
                redisTemplate.opsForValue().get("APPLE_STATE:" + state);

        if (storedNonce == null) {
            throw new IllegalStateException("Invalid or expired state");
        }

        redisTemplate.delete("APPLE_STATE:" + state);
        AuthResponseWithRefresh tokens = appleLoginService.appleLogin(code, user, storedNonce);
        redisTemplate.opsForValue().set(
                "APPLE_REFRESH" + tokens.getEmail(),
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

    @PostMapping("/apple-revoke")
    public ResponseEntity<?> revoke(@RequestHeader("Authorization") String token) {
        try {
            String email = appleLoginService.revoke(token);
            redisTemplate.delete("APPLE_REFRESH" + email);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}