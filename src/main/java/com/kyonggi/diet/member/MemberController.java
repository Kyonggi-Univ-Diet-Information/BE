package com.kyonggi.diet.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/form")
    public String form() {
        return "members/form";
    }

    @PostMapping("/join")
    public ResponseEntity<Member> join(MemberDTO memberDto) {
        Member member = Member.createMember(memberDto.getEmail(), memberDto.getSocialType(), memberDto.getName(), memberDto.getProfileUrl());
        memberService.joinMember(member);
        log.info("join member = {}", member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Member> delete(@PathVariable("id") Long memberId) {
        Optional<Member> member = memberService.findMemberId(memberId);
        memberService.deleteMember(member);
        log.info("deleted member = {}", member);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
