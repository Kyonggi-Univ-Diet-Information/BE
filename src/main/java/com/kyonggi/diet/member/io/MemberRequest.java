package com.kyonggi.diet.member.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequest {

    private String email;

    private String password;

    private String name;

    private String profileUrl;
}
