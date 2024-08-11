package com.kyonggi.diet.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String socialType;
    private String name;
    private String profileUrl;

    public static Member createMember(String email, String socialType, String name, String profileUrl) {
        Member member = new Member();
        member.setEmail(email);
        member.setSocialType(socialType);
        member.setName(name);
        member.setProfileUrl(profileUrl);
        return member;
    }
}
