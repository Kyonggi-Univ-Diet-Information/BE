package com.kyonggi.diet.auth.kakao.service;

import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.io.AuthResponseWithRefresh;
import com.kyonggi.diet.auth.kakao.dto.KakaoTokenResponse;
import com.kyonggi.diet.auth.kakao.dto.KakaoUserInfo;
import com.kyonggi.diet.auth.socialAccount.SocialAccount;
import com.kyonggi.diet.auth.socialAccount.SocialAccountRepository;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {

    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;
    private final CustomMembersDetailService membersDetailService;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final StringRedisTemplate redisTemplate;

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);

    /**
     * 카카오 로그인 서비스
     */
    public AuthResponseWithRefresh login(String code) {
        // 1. 카카오 토큰 발급
        KakaoTokenResponse tokenResponse = kakaoOAuthService.getToken(code);

        // 2. 사용자 정보 조회
        KakaoUserInfo userInfo =
                kakaoOAuthService.getUserInfo(tokenResponse.getAccessToken());

        SocialAccount social = socialAccountRepository
                .findByProviderAndProviderSub(Provider.KAKAO, userInfo.getProviderId())
                .orElse(null);

        MemberEntity member;

        // 3. 회원 조회 / 생성
        if (social != null) {
            member = social.getMember();
        } else {
            // email 기준 연결 or 신규
            member = memberRepository.findByEmail(userInfo.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(
                                    MemberEntity.builder()
                                            .email(userInfo.getEmail())
                                            .name(userInfo.getNickname())
                                            .build()
                            )
                    );

            social = SocialAccount.builder()
                    .member(member)
                    .provider(Provider.KAKAO)
                    .providerSub(userInfo.getProviderId())
                    .build();
        }

        if (userInfo.getEmail() != null &&
                !userInfo.getEmail().equals(member.getEmail())) {
            member.updateEmail(userInfo.getEmail());
        }

        if (tokenResponse.getAccessToken() != null &&
                !tokenResponse.getAccessToken().isBlank()) {
            social.updateToken(tokenResponse.getAccessToken());
            socialAccountRepository.save(social);
        }

        // 5. 우리 JWT 발급
        String accessJwt = jwtTokenUtil.generateAccessToken(
                membersDetailService.loadUserByUsername(member.getEmail())
        );
        String refreshJwt = jwtTokenUtil.generateRefreshToken(
                membersDetailService.loadUserByUsername(member.getEmail())
        );

        redisTemplate.opsForValue().set(
                "KAKAO_REFRESH" + member.getEmail(),
                refreshJwt,
                REFRESH_TOKEN_TTL
        );

        return new AuthResponseWithRefresh(accessJwt, refreshJwt, member.getEmail());
    }

    /**
     * 카카오 탈퇴 서비스
     */
    public void revoke(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Member not found: " + email)
                );

        // Kakao 소셜 계정 조회
        socialAccountRepository
                .findByMemberIdAndProvider(member.getId(), Provider.KAKAO)
                .ifPresent(sa -> {
                    kakaoOAuthService.revoke(sa.getProviderToken());
                    socialAccountRepository.delete(sa);
                });

        redisTemplate.delete("KAKAO_REFRESH" + email);

        // 다른 소셜 남아있는지 확인
        boolean hasOtherSocial = socialAccountRepository.existsByMemberId(member.getId());

        // 아무 소셜도 없으면 Member 삭제
        if (!hasOtherSocial) {
            memberRepository.delete(member);
        }
    }
}
