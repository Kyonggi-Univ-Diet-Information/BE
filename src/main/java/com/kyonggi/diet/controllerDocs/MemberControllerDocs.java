package com.kyonggi.diet.controllerDocs;

import com.kyonggi.diet.member.io.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface MemberControllerDocs {
    @Operation(summary = "전체 회원 조회", description = "DB에 저장된 모든 회원 정보 조회하는 API")
    public List<MemberResponse> getMembers();

    @Operation(summary = "특정 회원 1명 조회", description = "회원 ID 값으로 특정 회원 1명의 정보 조회하는 API")
    @Parameter(name = "id", description = "조회를 원하는 회원 ID")
    public MemberResponse getMember(@PathVariable Long id);

    @GetMapping("/my-page")
    @Operation(
            summary = "특정 회원 1명의 마이페이지 조회",
            description = "회원의 마이페이지 정보를 조회하는 API " +
                    "(이메일, 이름, 쓴 리뷰(긱사, 경슐), 좋아요 누른 리뷰(긱사, 경슐) 조회"
    )
    @Parameter(
            name = "Authorization",
            description = "Bearer 토큰 (예: 'Bearer eyJhbGci...')",
            required = true
    )
    ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String token);
}
