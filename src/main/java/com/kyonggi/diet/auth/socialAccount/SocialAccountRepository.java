package com.kyonggi.diet.auth.socialAccount;

import com.kyonggi.diet.auth.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderSub(Provider provider, String providerSub);
    Optional<SocialAccount> findByMemberIdAndProvider(Long memberId, Provider provider);
    boolean existsByMemberId(Long memberId);
}