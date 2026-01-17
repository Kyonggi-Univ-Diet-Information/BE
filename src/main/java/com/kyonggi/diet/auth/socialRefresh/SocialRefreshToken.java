package com.kyonggi.diet.auth.socialRefresh;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialRefreshToken {
    @Id @Column(name = "member_id")
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(name = "fk_member_social_refresh")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider;

    private String refreshToken;

    public void updateRefreshToken(String refreshTokenValue) {
        this.refreshToken = refreshTokenValue;
    }
}