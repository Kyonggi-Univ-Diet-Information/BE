package com.kyonggi.diet.member.service;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMembersDetailService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found for the email" + email));
        return new User(memberEntity.getEmail(), memberEntity.getPassword(), new ArrayList<>());
    }
}
