package com.kyonggi.diet.auth.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomMembersDetailService customMembersDetailService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public GoogleIdToken.Payload verify(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(List.of(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new RuntimeException("Invalid Google ID Token");
            }

            return token.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Google ID Token verification failed");
        }
    }

    public AuthResponse login(String code) {

        // 1. code 검증
        if (code == null || code.isBlank()) {
            log.error("Authorization code is missing");
            return new AuthResponse(null, "Authorization code is missing");
        }

        try {
            // 2. code → google token
            JsonNode tokenResponse = googleOAuthClient.exchangeCode(code);
            if (tokenResponse == null || tokenResponse.get("id_token") == null) {
                log.error("Failed to get id_token from Google");
                return new AuthResponse(null, "Failed to get google token");
            }

            String idToken = tokenResponse.get("id_token").asText();

            // 3. id_token 검증
            GoogleIdToken.Payload payload = verify(idToken);

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            if (email == null || email.isBlank()) {
                log.error("Email not found in Google payload");
                return new AuthResponse(null, "Invalid google account");
            }

            // 4. 유저 upsert
            memberRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("New google user registered: {}", email);
                        new MemberEntity();
                        return memberRepository.save(
                                MemberEntity
                                        .builder()
                                        .email(email)
                                        .name(name)
                                        .build()
                        );
                    });

            // 5. JWT 발급
            String accessToken = jwtTokenUtil.generateToken(
                    customMembersDetailService.loadUserByUsername(email)
            );

            return new AuthResponse(accessToken, "SUCCESS");

        } catch (Exception e) {
            log.error("Error during Google login process", e);
            throw new RuntimeException("Internal server error");
            //return new AuthResponse(null, "Internal server error");
        }
    }
}
