package com.kyonggi.diet.auth.kakao.service;

import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.kakao.dto.KakaoTokenResponse;
import com.kyonggi.diet.auth.kakao.dto.KakaoUserInfo;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshToken;
import com.kyonggi.diet.auth.socialRefresh.SocialRefreshTokenRepository;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {

    private final KakaoOAuthService kakaoOAuthService;
    private final MemberRepository memberRepository;
    private final SocialRefreshTokenRepository socialRefreshTokenRepository;
    private final CustomMembersDetailService membersDetailService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 카카오 로그인 서비스
     */
    public AuthResponse login(String code) {
        // 1. 카카오 토큰 발급
        KakaoTokenResponse tokenResponse = kakaoOAuthService.getToken(code);

        // 2. 사용자 정보 조회
        KakaoUserInfo userInfo =
                kakaoOAuthService.getUserInfo(tokenResponse.getAccessToken());

        // 3. 회원 조회 / 생성
        MemberEntity member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() ->
                        memberRepository.saveAndFlush(
                                MemberEntity.builder()
                                        .email(userInfo.getEmail())
                                        .name(userInfo.getNickname())
                                        .build()
                        )
                );

        // 4. 카카오 access_token 저장 (SocialRefreshToken.refreshToken)
        saveOrUpdateSocialToken(member, tokenResponse.getAccessToken());

        // 5. 우리 JWT 발급
        String jwt = jwtTokenUtil.generateToken(
                membersDetailService.loadUserByUsername(member.getEmail())
        );

        return new AuthResponse(jwt, member.getEmail());
    }

    private void saveOrUpdateSocialToken(MemberEntity member, String accessToken) {
        if (accessToken == null) return;
        SocialRefreshToken token =
                socialRefreshTokenRepository.findByMemberId(member.getId())
                        .orElse(null);

        if (token == null) {
            token = SocialRefreshToken.builder()
                    .member(member)
                    .provider(Provider.KAKAO)
                    .refreshToken(accessToken)
                    .build();
            socialRefreshTokenRepository.save(token);
        } else {
            token.updateRefreshToken(accessToken);
        }
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

        // 1. 카카오 unlink
        kakaoOAuthService.revoke(member);

        // 2. 회원 삭제 (SocialRefreshToken CASCADE)
        memberRepository.delete(member);
    }
}
