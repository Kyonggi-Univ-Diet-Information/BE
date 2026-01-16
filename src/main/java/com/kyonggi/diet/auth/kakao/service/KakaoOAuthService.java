package com.kyonggi.diet.auth.kakao.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyonggi.diet.auth.kakao.dto.KakaoTokenResponse;
import com.kyonggi.diet.auth.kakao.dto.KakaoUserInfo;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshToken;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshTokenRepository;
import com.kyonggi.diet.member.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final SocialRefreshTokenRepository socialRefreshTokenRepository;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    /**
     * 카카오로부터 access, refresh 토큰 획득
     */
    public KakaoTokenResponse getToken(String code) {
        try {
            BufferedReader br = getBufferedReader(code);

            String result = br.lines().reduce("", String::concat);
            JsonObject obj = JsonParser.parseString(result).getAsJsonObject();

            return new KakaoTokenResponse(
                    obj.get("access_token").getAsString(),
                    obj.has("refresh_token")
                            ? obj.get("refresh_token").getAsString()
                            : null
            );
        } catch (Exception e) {
            throw new IllegalStateException("Kakao token request failed", e);
        }
    }

    /**
     * 토큰 획득을 위한 버퍼 리더 생성
     */
    private BufferedReader getBufferedReader(String code) throws IOException {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        BufferedWriter bw =
                new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(
                "grant_type=authorization_code"
                        + "&client_id=" + clientId
                        + "&redirect_uri=" + redirectUri
                        + "&code=" + code
        );
        bw.flush();

        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    /**
     * 유저 정보 획득
     */
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            URL url = new URL(USER_INFO_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = br.lines().reduce("", String::concat);
            JsonObject root = JsonParser.parseString(result).getAsJsonObject();

            JsonObject properties = root.getAsJsonObject("properties");
            JsonObject account = root.getAsJsonObject("kakao_account");

            return new KakaoUserInfo(
                    account.get("email").getAsString(),
                    properties.get("nickname").getAsString()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Kakao user info request failed", e);
        }
    }

    /**
     * 카카오 계정 revoke
     */
    public void revoke(MemberEntity member) {
        SocialRefreshToken token = socialRefreshTokenRepository
                .findByMemberId(member.getId())
                .orElseThrow(() ->
                        new UsernameNotFoundException("SocialRefreshToken not found")
                );

        unlink(token.getRefreshToken());
    }

    /**
     * 카카오 계정 연결 해제 요청
     */
    private void unlink(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                UNLINK_URL,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Kakao unlink failed");
        }
    }
}
