package com.kyonggi.diet.member;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired EntityManager em;

    @Test
    public void joinMember() {
        Member member = Member.createMember("test@example.com", "GOOGLE", "Shuhua", "http://test.com");
        Member savedMember = memberService.joinMember(member);
        assertThat(member.getEmail()).isEqualTo(savedMember.getEmail());

        String matchingMemberEmail = em.createQuery("select m.email from Member m where m.email = :email", String.class)
                .setParameter("email", member.getEmail())
                .getSingleResult();

        assertThat(member.getEmail()).isEqualTo(matchingMemberEmail);
    }

    // 중복된 멤버 에러 확인
    @Test
    public void joinDuplicatedMember() {
        Member member = Member.createMember("test@example.com", "GOOGLE", "Shuhua", "http://test.com");
        memberService.joinMember(member);

        Assertions.assertThatThrownBy(() -> {
            memberService.joinMember(member);
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void deleteMember() {
        Member member = Member.createMember("test@example.com", "GOOGLE", "Shuhua", "http://test.com");
        memberService.joinMember(member);
        memberService.deleteMember(Optional.of(member));

        Optional<Member> foundMember = memberService.findMemberId(member.getId());

        assertThat(foundMember).isEmpty();
    }
}
