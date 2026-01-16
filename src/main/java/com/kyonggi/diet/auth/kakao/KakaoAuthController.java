package com.kyonggi.diet.auth.kakao;

import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.kakao.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카카오 인증 API", description = "카카오 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

/*    @GetMapping("/kakao-form")
    public Map<String, String> kakaoForm() {
        log.info("call kakao-form api");
        String redirectUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        return Map.of("url", redirectUrl);
    }*/

    @GetMapping("/kakao-form")
    public RedirectView kakaoForm() {
        log.info("call kakao-form api");
        String redirectUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/kakao-login")
    public AuthResponse kakaoLogin(@RequestParam("code") String code) {
        return kakaoLoginService.login(code);
    }

    @PostMapping("/kakao-revoke")
    public void revoke(@RequestHeader("Authorization") String authorizationHeader) {
        kakaoLoginService.revoke(authorizationHeader);
    }
}
