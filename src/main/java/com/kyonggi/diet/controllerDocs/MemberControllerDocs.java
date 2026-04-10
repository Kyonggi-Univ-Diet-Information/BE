package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.member.DTO.UpdateNicknameRequest;
import com.kyonggi.diet.member.io.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface MemberControllerDocs {
    @Operation(summary = "전체 회원 조회", description = "DB에 저장된 모든 회원 정보 조회하는 API")
    public List<MemberResponse> getMembers();

    @Operation(summary = "특정 회원 1명 조회", description = "회원 ID 값으로 특정 회원 1명의 정보 조회하는 API")
    @Parameter(name = "id", description = "조회를 원하는 회원 ID")
    public MemberResponse getMember(@PathVariable Long id);

    @Operation(summary = "닉네임 유효 확인", description = "해당 닉네임이 유효한지 확인(2~15자의 대소문자 영어 및 한국어 및 숫자 가능), 비속어 불가능, 중복 체크")
    @Parameter(name = "nickname", description = "닉네임")
    ResponseEntity<?> checkNickname(
            @RequestParam String nickname
    );

    @Operation(summary = "닉네임 업데이트", description = "회원 닉네임 변경")
    ResponseEntity<?> updateNickname(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody UpdateNicknameRequest request

    );
}
