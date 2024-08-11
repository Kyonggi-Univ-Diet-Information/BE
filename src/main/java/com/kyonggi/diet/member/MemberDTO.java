package com.kyonggi.diet.member;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberDTO {
    private String email;
    private String socialType;
    private String name;
    private String profileUrl;
}
