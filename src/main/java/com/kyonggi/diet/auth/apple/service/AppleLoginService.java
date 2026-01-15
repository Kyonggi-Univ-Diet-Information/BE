package com.kyonggi.diet.auth.apple.service;

import com.kyonggi.diet.auth.apple.dto.AppleDto;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    public AuthResponse appleLogin(String code, String userJson, String expectedNonce) throws Exception {
        AppleDto appleDto = appleOAuthClient.getAppleInfo(code, expectedNonce);
        String name = appleOAuthClient.extractNameFromUser(userJson);

        MemberEntity member = memberRepository.findByEmail(appleDto.getEmail())
                .orElseGet(() -> memberRepository.save(
                        MemberEntity.builder()
                                .email(appleDto.getEmail())
                                .appleSub(appleDto.getSub())
                                .name(name)
                                .build()
                ));

        String jwt = jwtTokenUtil.generateToken(
                customMembersDetailService.loadUserByUsername(member.getEmail())
        );

        return new AuthResponse(member.getEmail(), jwt);
    }
}