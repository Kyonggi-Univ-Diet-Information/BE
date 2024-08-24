package com.kyonggi.diet.member;

import com.kyonggi.diet.member.io.MemberResponse;
import com.kyonggi.diet.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 멤버 컨트롤러 구현
 * @author boroboro01
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    /**
     * 모든 멤버 정보를 반환합니다.
     * @return list
     */
    @GetMapping("/members")
    public List<MemberResponse> getMembers() {
        log.info("API GET /members called");
        List<MemberDTO> list = memberService.getAllMembers();
        log.info("List of members = {}", list);
        List<MemberResponse> responses = list.stream().map(memberDTO -> mapToMemberReponse(memberDTO)).collect(Collectors.toList());
        return responses;
    }

    /**
     * MemberDTO 에서 MemberResponse 으로 변환하는 Mapper Method 입니다.
     * @param memberDTO (member DTO)
     * @return MemberResponse
     */
    private MemberResponse mapToMemberReponse(MemberDTO memberDTO) {
        return modelMapper.map(memberDTO, MemberResponse.class);
    }
}
