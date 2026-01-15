package com.kyonggi.diet.auth.socialRefresh;

import com.kyonggi.diet.auth.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRefreshTokenRepository extends JpaRepository<SocialRefreshToken, Long> {

    Optional<SocialRefreshToken> findByMemberId(Long memberId);

    Optional<SocialRefreshToken> findByProvider(Provider provider);
}
