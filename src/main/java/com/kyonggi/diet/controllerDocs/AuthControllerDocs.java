package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.auth.io.AuthRequest;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.member.io.MemberRequest;
import com.kyonggi.diet.member.io.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "회원 요청 정보에 맞게 회원가입하여 회원 값을 DB에 저장하는 API")
    @Parameter(name = "email", description = "이메일")
    @Parameter(name = "password", description = "비밀번호")
    @Parameter(name = "name", description = "이름")
    @Parameter(name = "profileUrl", description = "프로필 URL")
    public MemberResponse register(@RequestBody MemberRequest memberRequest);

    @Operation(summary = "로그인", description = "DB에 저장된 특정 회원의 이메일과 비밀번호로 로그인하는 API")
    @Parameter(name = "email", description = "이메일")
    @Parameter(name = "password", description = "비밀번호")
    public AuthResponse authenticateProfile(@RequestBody AuthRequest authRequest);
}
