package com.kyonggi.diet.auth;

import com.kyonggi.diet.auth.io.AuthRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.AuthControllerDocs;
import com.kyonggi.diet.member.DTO.MemberDTO;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "인증 API", description = "회원 인증(회원가입, 로그인 등)에 대한 API 입니다.")
public class AuthController implements AuthControllerDocs {

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

    @PostMapping("/token/refresh")
    public AuthResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        // 1. refreshToken 유효성 체크
        if (jwtTokenUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails user = membersDetailService.loadUserByUsername(username);

        // 2. 새 Access Token 생성
        String newAccessToken = jwtTokenUtil.generateAccessToken(user);

        // 3. 응답
        return new AuthResponse(newAccessToken, username);
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
