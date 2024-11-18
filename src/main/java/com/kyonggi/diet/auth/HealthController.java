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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "상태 API", description = "상태 확인하는 API 입니다.")
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<String> homeCheck() {
        return new ResponseEntity<>("Home Screen", HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
