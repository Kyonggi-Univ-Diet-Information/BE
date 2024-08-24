package com.kyonggi.diet.member.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {

    private String email;

    private String password;

    private String name;

    private String profileUrl;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
