package com.kyonggi.diet.auth;

import com.kyonggi.diet.auth.io.AuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AuthResponse kakaoLogin(@RequestParam(value="code", required = false) String code, HttpSession session, RedirectAttributes rttr) throws Exception {
        log.info("#######{}", code);
        String accessToken = kakaoAuthService.getAccessToken(code);
        AuthResponse response = kakaoAuthService.getUserInfo(accessToken, session, rttr);
        log.info("AuthResponse: {}", response); // AuthResponse 객체 확인
        return response;
    }
}
