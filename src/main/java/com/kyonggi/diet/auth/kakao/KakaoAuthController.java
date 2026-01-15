package com.kyonggi.diet.auth.kakao;

import com.kyonggi.diet.auth.io.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카카오 인증 API", description = "카카오 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class KakaoAuthController {

    final private KakaoAuthService kakaoAuthService;

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
    public AuthResponse kakaoLogin(@RequestParam(value="code", required = false) String code, HttpSession session, RedirectAttributes rttr) {
        log.info("Received authorization code: {}", code);

        // 1. 인증 코드 검증
        if (code == null || code.isBlank()) {
            log.error("Authorization code is missing");
            return new AuthResponse(null, "Authorization code is missing");
        }

        try {
            // 2. 액세스 토큰 요청
            String accessToken = kakaoAuthService.getAccessToken(code);
            if (accessToken == null || accessToken.isBlank()) {
                log.error("Failed to get access token from Kakao");
                return new AuthResponse(null, "Failed to get access token");
            }

            // 3. 사용자 정보 요청
            AuthResponse response = kakaoAuthService.getUserInfo(accessToken, session, rttr);
            if (response == null) {
                log.error("Failed to fetch user info from Kakao");
                return new AuthResponse(null, "Failed to fetch user info");
            }

            log.info("AuthResponse: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return new AuthResponse(null, "Internal server error");
        }
    }

}
