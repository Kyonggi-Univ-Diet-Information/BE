package com.kyonggi.diet.auth;

import com.kyonggi.diet.auth.io.AuthRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.io.MemberRequest;
import com.kyonggi.diet.member.io.MemberResponse;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import com.kyonggi.diet.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "인증 API", description = "회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class AuthController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomMembersDetailService membersDetailService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public MemberResponse register(@RequestBody MemberRequest memberRequest) {
        log.info("API GET /register called");
        MemberDTO memberDTO = mapToMemberDTO(memberRequest);
        memberDTO = memberService.createMember(memberDTO);
        log.info("Member DTO details {}", memberDTO);
        return mapToMemberReponse(memberDTO);
    }

    @PostMapping("/login")
    public AuthResponse authenticateProfile(@RequestBody AuthRequest authRequest) {
        log.info("API POST /login called");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        final UserDetails user = membersDetailService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(user);
        return new AuthResponse(token, authRequest.getEmail());
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
