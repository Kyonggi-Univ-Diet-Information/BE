package com.kyonggi.diet.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.auth.google.dto.GoogleTokenDto;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshToken;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshTokenRepository;
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
    private final SocialRefreshTokenRepository socialRefreshTokenRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Transactional
    public AuthResponse login(String code) {
        try {
            // 1. code → token
            GoogleTokenDto tokenResponse = googleOAuthClient.login(code);

            log.info("token: {}", tokenResponse.getAccessToken());
            log.info("token: {}", tokenResponse.getRefreshToken());

            // 3. 유저 upsert (sub 기준)
            MemberEntity member = getMemberFromGoogleSub(tokenResponse);

            // 4. refresh token 최초 저장
            saveOrUpdateRefreshToken(member, tokenResponse.getRefreshToken());

            // 5. JWT 발급
            String accessToken = jwtTokenUtil.generateToken(
                    customMembersDetailService.loadUserByUsername(member.getEmail())
            );

            return new AuthResponse(accessToken, member.getEmail());

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
    public MemberEntity getMemberFromGoogleSub(GoogleTokenDto tokenResponse) {
        GoogleIdToken.Payload payload = verifyIdToken(tokenResponse.getIdToken());

        String googleSub = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        if (googleSub == null || googleSub.isBlank()) {
            throw new IllegalArgumentException("google sub missing");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("google email missing");
        }

        // 1) sub로 먼저 조회
        return memberRepository.findByGoogleSub(googleSub)
                .orElseGet(() -> {
                    // 2) 없으면 email로 조회 (기존 계정 있으면 연결)
                    return memberRepository.findByEmail(email)
                            .map(existing -> {
                                // 이미 다른 googleSub가 있으면? (보안상 케이스)
                                if (existing.getGoogleSub() != null && !existing.getGoogleSub().isBlank()
                                        && !existing.getGoogleSub().equals(googleSub)) {
                                    throw new IllegalStateException("email already linked to another google account");
                                }
                                existing.updateGoogleSub(googleSub); // setter/메서드 필요
                                if (existing.getName() == null || existing.getName().isBlank()) {
                                    existing.updateName(name);
                                }
                                return existing; // @Transactional이면 dirty checking으로 update됨
                            })
                            // 3) email도 없으면 신규 생성
                            .orElseGet(() -> memberRepository.save(
                                    MemberEntity.builder()
                                            .googleSub(googleSub)
                                            .email(email)
                                            .name(name)
                                            .build()
                            ));
                });
    }



    public void saveOrUpdateRefreshToken(MemberEntity member, String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            log.warn("No refresh token provided for memberId={}", member.getId());
            return;
        }

        SocialRefreshToken socialRefreshToken = socialRefreshTokenRepository.findByMemberId(member.getId())
                .orElseGet(() -> SocialRefreshToken.builder()
                        .member(member)
                        .provider(Provider.GOOGLE)
                        .refreshToken(refreshTokenValue)
                        .build()
                );

        socialRefreshToken.updateRefreshToken(refreshTokenValue);
        socialRefreshTokenRepository.save(socialRefreshToken);
    }

    @Transactional
    public void revokeToken(String jwt) {
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        Long memberId = member.getId();

        // 1. refresh token 조회
        socialRefreshTokenRepository.findByMemberId(memberId)
                .ifPresent(token -> {
                    if (token.getProvider() == Provider.GOOGLE) {
                        try {
                            googleOAuthClient.revokeToken(token.getRefreshToken());
                        } catch (Exception e) {
                            // revoke 실패해도 탈퇴는 진행
                            log.warn("Google revoke failed. memberId={}", memberId, e);
                        }
                    }
                });

        // 2. 회원 삭제 (CASCADE로 refresh token 같이 삭제)
        memberRepository.delete(member);
    }
}
