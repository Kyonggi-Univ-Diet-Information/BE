package com.kyonggi.diet.member.service;

import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMembersDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /*@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found for the email: " + email));

        // 비밀번호가 없는 경우 기본값 설정
        String password = memberEntity.getPassword() != null ? memberEntity.getPassword() : "SOCIAL_LOGIN_PASSWORD";

        // UserDetails 반환
        return new User(memberEntity.getEmail(), password, new ArrayList<>());
    }*/

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found for the email: " + email));

        // CustomUserDetails 반환
        return new CustomUserDetails(memberEntity);
    }

}
