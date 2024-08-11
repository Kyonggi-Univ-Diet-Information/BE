package com.kyonggi.diet.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member joinMember(Member member) {
        validation(member);
        memberRepository.save(member);
        return member;
    }

    // Member 중복 확인
    private void validation(Member member) {
        memberRepository.findByEmail(member.getEmail())
                        .ifPresent(m -> {
                            throw new IllegalStateException("Member already exists");
                        });
    }

    public Optional<Member> findMemberEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findMemberId(Long id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAllMember() {
        return memberRepository.findAll();
    }

//    public void modifyMember(Member member, MemberDTO memberDTO) {
//        member.setName(memberDTO.getName());
//        member.setProfileUrl(memberDTO.getProfileUrl());
//    }

    @Transactional
    public void deleteMember(Optional<Member> member) {
        member.ifPresent(m -> {
            memberRepository.delete(m);
        });
    }
}
