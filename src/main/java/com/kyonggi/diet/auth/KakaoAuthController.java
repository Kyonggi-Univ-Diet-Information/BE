package com.kyonggi.diet.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카카오 인증 API", description = "카카오 소셜 회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class KakaoAuthController {

    private KakaoAuthService kakaoAuthService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @RequestMapping("/kakao-form")
    public @ResponseBody String kakaoForm() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+clientId+"&redirect_uri="+redirectUri;
    }

    @GetMapping("/kakao-login")
    public String kakaoLogin(@RequestParam(value="code", required = false) String code, HttpSession session, RedirectAttributes rttr) throws Exception {
        log.info("#######{}", code);
        String accessToken = kakaoAuthService.getAccessToken(code);
        return kakaoAuthService.getUserInfo(accessToken, session, rttr);
    }
}