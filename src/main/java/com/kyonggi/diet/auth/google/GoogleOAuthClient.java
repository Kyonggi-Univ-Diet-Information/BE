package com.kyonggi.diet.auth.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kyonggi.diet.auth.google.dto.GoogleTokenDto;
import com.kyonggi.diet.member.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;


@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public GoogleTokenDto login(String code) {
        return requestToken(code);
    }

    private GoogleTokenDto requestToken(String code) {
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(
                        "code=" + code +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&redirect_uri=" + redirectUri +
                        "&grant_type=authorization_code"
                )
                .retrieve()
                .bodyToMono(GoogleTokenDto.class)
                .block();

    }

    public void revokeToken(String refreshToken) {
        webClient.post()
                .uri("https://oauth2.googleapis.com/revoke")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(
                        "token=" + refreshToken
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public GoogleTokenDto parseIdToken(String idToken, String clientId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("oauth2.googleapis.com")
                        .path("/tokeninfo")
                        .queryParam("id_token", idToken)
                        .build())
                .retrieve()
                .bodyToMono(GoogleTokenDto.class)
                .block();
    }

}

