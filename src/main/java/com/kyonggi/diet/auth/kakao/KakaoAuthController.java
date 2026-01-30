package com.kyonggi.diet.auth.kakao;

import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.io.AuthResponseWithRefresh;
import com.kyonggi.diet.auth.kakao.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카카오 인증 API", description = "카카오 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
        private static final long COOKIE_AGE_SECONDS = REFRESH_TOKEN_TTL.getSeconds();

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @GetMapping("/kakao-form")
    public RedirectView kakaoForm() {
        log.info("call kakao-form api");
        String redirectUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/kakao-login")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
        AuthResponseWithRefresh tokens = kakaoLoginService.login(code);

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

    @PostMapping("/kakao-revoke")
    public ResponseEntity<?> revoke(@RequestHeader("Authorization") String authorizationHeader) {
        kakaoLoginService.revoke(authorizationHeader);

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
    }
}
