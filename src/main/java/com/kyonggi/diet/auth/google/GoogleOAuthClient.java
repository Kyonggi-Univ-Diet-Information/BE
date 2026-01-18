package com.kyonggi.diet.auth.google;

import com.kyonggi.diet.auth.google.dto.GoogleTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final WebClient webClient = WebClient.builder().build();

    //@Value("${google.client-id}")
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    //@Value("${google.client-secret}")
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    //@Value("${google.redirect-uri}")
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
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

