package com.kyonggi.diet.auth;

import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.io.MemberRequest;
import com.kyonggi.diet.member.io.MemberResponse;
import com.kyonggi.diet.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public MemberResponse register(@RequestBody MemberRequest memberRequest) {
        log.info("API GET /register called");
        MemberDTO memberDTO = mapToMemberDTO(memberRequest);
        memberDTO = memberService.createMember(memberDTO);
        log.info("Member DTO details {}", memberDTO);
        return mapToMemberReponse(memberDTO);
    }



    /**
     * MemberDTO 에서 MemberResponse 으로 변환하는 Mapper Method 입니다.
     * @param memberDTO (member DTO)
     * @return MemberResponse
     */
    private MemberResponse mapToMemberReponse(MemberDTO memberDTO) {
        return modelMapper.map(memberDTO, MemberResponse.class);
    }

    private MemberDTO mapToMemberDTO(MemberRequest memberRequest) {
        return modelMapper.map(memberRequest, MemberDTO.class);
    }
}
