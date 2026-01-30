package com.kyonggi.diet.auth.apple.service;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.auth.apple.dto.AppleDto;
import com.kyonggi.diet.auth.apple.dto.AppleLoginRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.io.AuthResponseWithRefresh;
import com.kyonggi.diet.auth.socialAccount.SocialAccount;
import com.kyonggi.diet.auth.socialAccount.SocialAccountRepository;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
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
public class AppleLoginService {
    private final AppleOAuthClient appleOAuthClient;
    private final MemberRepository memberRepository;
    private final CustomMembersDetailService customMembersDetailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final SocialAccountRepository socialAccountRepository;

    /**
     * 애플 로그인
     */
    public AuthResponseWithRefresh appleLogin(
            String code, AppleLoginRequest.AppleUser user, String expectedNonce
    ) throws Exception {
        AppleDto appleDto = appleOAuthClient.getAppleInfo(code, expectedNonce);
        String name = extractNameFromUser(user);

        SocialAccount social = socialAccountRepository
                .findByProviderAndProviderSub(Provider.APPLE, appleDto.getSub())
                .orElse(null);

        MemberEntity member;

        if (social != null) {
            member = social.getMember();
        } else {
            // email 기준 연결 or 신규
            member = memberRepository.findByEmail(appleDto.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(
                                    MemberEntity.builder()
                                            .email(appleDto.getEmail())
                                            .name(name)
                                            .build()
                            )
                    );

            social = SocialAccount.builder()
                    .member(member)
                    .provider(Provider.APPLE)
                    .providerSub(appleDto.getSub())
                    .build();
        }

        // email 변경 대응
        if (appleDto.getEmail() != null &&
                !appleDto.getEmail().equals(member.getEmail())) {
            member.updateEmail(appleDto.getEmail());
        }

        if (appleDto.getRefresh_token() != null &&
                !appleDto.getRefresh_token().isBlank()) {
            social.updateToken(appleDto.getRefresh_token());
            socialAccountRepository.save(social);
        }

        String accessJwt = jwtTokenUtil.generateAccessToken(
                customMembersDetailService.loadUserByUsername(member.getEmail())
        );

        String refreshJwt = jwtTokenUtil.generateRefreshToken(
                customMembersDetailService.loadUserByUsername(member.getEmail())
        );

        return new AuthResponseWithRefresh(accessJwt, refreshJwt, appleDto.getEmail());
    }

    /**
     * 애플 REVOKE
     */
    public String revoke(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Member not found: " + email)
                );

        // Apple 소셜 계정 조회
        socialAccountRepository
                .findByMemberIdAndProvider(member.getId(), Provider.APPLE)
                .ifPresent(sa -> {
                    // Apple revoke 호출
                    appleOAuthClient.userRevoke(sa.getProviderToken());

                    // SocialAccount 삭제
                    socialAccountRepository.delete(sa);
                });

        // 다른 소셜 남아있는지 확인
        boolean hasOtherSocial = socialAccountRepository.existsByMemberId(member.getId());

        // 아무 소셜도 없으면 Member 삭제
        if (!hasOtherSocial) {
            memberRepository.delete(member);
        }
        return email;
    }

    private String extractNameFromUser(AppleLoginRequest.AppleUser user) {
        if (user == null || user.getName() == null) return null;

        String first = user.getName().getFirstName();
        String last = user.getName().getLastName();

        String merged = ((last != null ? last : "") +
                (first != null ? first : "")).trim();

        return merged.isBlank() ? null : merged;
    }
}