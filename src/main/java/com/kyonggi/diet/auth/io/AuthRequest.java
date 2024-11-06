package com.kyonggi.diet.auth.io;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "로그인 요청 DTO")
public class AuthRequest {

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "비밀 번호")
    private String password;
}
