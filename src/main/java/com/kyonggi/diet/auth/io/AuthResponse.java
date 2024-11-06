package com.kyonggi.diet.auth.io;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class AuthResponse {

    @Schema(description = "토큰")
    private String token;

    @Schema(description = "이메일")
    private String email;
}
