package com.kyonggi.diet.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.auth.google.dto.GoogleTokenDto;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.socialAccount.SocialAccount;
import com.kyonggi.diet.auth.socialAccount.SocialAccountRepository;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomMembersDetailService customMembersDetailService;
    private final SocialAccountRepository socialAccountRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Transactional
    public AuthResponse login(String code) {
        try {
            // 1. code → token
            GoogleTokenDto tokenResponse = googleOAuthClient.login(code);

            // 2. id_token 검증
            GoogleIdToken.Payload payload = verifyIdToken(tokenResponse.getIdToken());

            String googleSub = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            if (googleSub == null || googleSub.isBlank()) {
                throw new IllegalArgumentException("google sub missing");
            }

            // 3. SocialAccount 기준 로그인
            SocialAccount social = socialAccountRepository
                    .findByProviderAndProviderSub(Provider.GOOGLE, googleSub)
                    .orElse(null);

            MemberEntity member;

            if (social != null) {
                member = social.getMember();
            } else {
                // email 기준 기존 회원 연결 or 신규 생성
                member = memberRepository.findByEmail(email)
                        .orElseGet(() ->
                                memberRepository.save(
                                        MemberEntity.builder()
                                                .email(email)
                                                .name(name)
                                                .build()
                                )
                        );

                social = SocialAccount.builder()
                        .member(member)
                        .provider(Provider.GOOGLE)
                        .providerSub(googleSub)
                        .build();
            }

            if (email != null &&
                    !email.equals(member.getEmail())) {
                member.updateEmail(email);
            }

            // refresh token 저장/갱신
            if (tokenResponse.getRefreshToken() != null &&
                    !tokenResponse.getRefreshToken().isBlank()) {
                social.updateToken(tokenResponse.getRefreshToken());
                socialAccountRepository.save(social);
            }

            // 4. JWT 발급
            String jwt = jwtTokenUtil.generateToken(
                    customMembersDetailService.loadUserByUsername(member.getEmail())
            );

            return new AuthResponse(jwt, member.getEmail());

        } catch (IllegalArgumentException e) {
            log.warn("Google login failed: {}", e.getMessage());
            return new AuthResponse(null, "");

        } catch (Exception e) {
            log.error("Error during Google login process", e);
            throw new RuntimeException("Internal server error");
        }
    }

    private GoogleIdToken.Payload verify(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new IllegalArgumentException("Invalid Google ID Token");
            }
            return token.getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Google ID Token verification failed", e);
        }
    }

    private GoogleIdToken.Payload verifyIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) throw new IllegalArgumentException("Invalid Google ID token");
            return token.getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Google ID token", e);
        }
    }

    @Transactional
    public void revokeToken(String jwt) {
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));

        // Google 소셜 계정 조회
        socialAccountRepository
                .findByMemberIdAndProvider(member.getId(), Provider.GOOGLE)
                .ifPresent(sa -> {
                    try {
                        googleOAuthClient.revokeToken(sa.getProviderToken());
                    } catch (Exception e) {
                        log.warn("Google revoke failed. memberId={}", member.getId(), e);
                    }
                    socialAccountRepository.delete(sa);
                });

        // 다른 소셜 남아있는지 확인
        boolean hasOtherSocial =
                socialAccountRepository.existsByMemberId(member.getId());

        if (!hasOtherSocial) {
            memberRepository.delete(member);
        }
    }
}
