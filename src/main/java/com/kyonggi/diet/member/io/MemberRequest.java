package com.kyonggi.diet.member.io;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원 요청 DTO")
public class MemberRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Provide valid email address")
    @Schema(description = "이메일")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Schema(description = "비밀번호")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 10, message = "Name should be at most 10 characters")
    @Schema(description = "이름")
    private String name;

    @Schema(description = "프로필 ID")
    private String profileUrl;
}
