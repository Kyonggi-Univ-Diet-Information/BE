package com.kyonggi.diet.member.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원 DTO")
public class MemberDTO {
    @Schema(description = "프로필 ID")
    private String profileId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "프로필 URL")
    private String profileUrl;

    @Schema(description = "생성 타임 스탬프")
    private Timestamp createdAt;

    @Schema(description = "업데이트 타임 스탬프")
    private Timestamp updatedAt;
}
