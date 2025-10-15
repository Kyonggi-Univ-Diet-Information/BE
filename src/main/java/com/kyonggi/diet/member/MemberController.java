package com.kyonggi.diet.member;

import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.controllerDocs.MemberControllerDocs;
import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.io.MemberRequest;
import com.kyonggi.diet.member.io.MemberResponse;
import com.kyonggi.diet.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 멤버 컨트롤러 구현
 * @author boroboro01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
@Tag(name = "회원 API", description = "회원에 대한 API 입니다.")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
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
        return list.stream().map(this::mapToMemberReponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MemberResponse getMember(@PathVariable Long id) {
        log.info("API GET /members/{} called", id);
        MemberDTO memberDTO = memberService.getMemberById(id);
        log.info("Member = {}", memberDTO);
        return mapToMemberReponse(memberDTO);
    }

    @GetMapping("/my-page")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or malformed");
        }

        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return ResponseEntity.ok(memberService.getMyPage(email));
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
