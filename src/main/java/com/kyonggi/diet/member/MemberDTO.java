package com.kyonggi.diet.member;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private String profileId;

    private String email;

    private String password;

    private String name;

    private String profileUrl;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
