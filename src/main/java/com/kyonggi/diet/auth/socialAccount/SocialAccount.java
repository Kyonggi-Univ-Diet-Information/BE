package com.kyonggi.diet.auth.socialAccount;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerSub"})
    }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String providerSub;

    // 탈퇴용 토큰 (Apple: refresh / Google: access / KaKao: access)
    @Column
    private String providerToken;

    public void updateToken(String token) {
        this.providerToken = token;
    }
}
