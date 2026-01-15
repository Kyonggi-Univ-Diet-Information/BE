package com.kyonggi.diet.auth.apple.service;

import com.kyonggi.diet.auth.apple.dto.AppleDto;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;

    public AuthResponse appleLogin(String code, String userJson, String expectedNonce) throws Exception {
        AppleDto appleDto = appleOAuthClient.getAppleInfo(code, expectedNonce);
        String name = appleOAuthClient.extractNameFromUser(userJson);

        MemberEntity member = memberRepository.findByEmail(appleDto.getEmail())
                .orElseGet(() -> memberRepository.saveAndFlush(
                        MemberEntity.builder()
                                .email(appleDto.getEmail())
                                .appleSub(appleDto.getSub())
                                .name(name)
                                .build()
                ));

        appleOAuthClient.saveOrUpdateRefreshToken(member, appleDto.getRefresh_token());

        String jwt = jwtTokenUtil.generateToken(
                customMembersDetailService.loadUserByUsername(member.getEmail())
        );

        return new AuthResponse(jwt, appleDto.getEmail());
    }

    public void revoke(String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Member not found: " + email)
                );
        // 1. Apple revoke
        appleOAuthClient.userRevoke(member);
        // 2. 회원 삭제 (RefreshToken은 CASCADE)
        memberRepository.delete(member);
    }
}