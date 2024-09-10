package com.kyonggi.diet.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /**
     * 이메일을 통해 데이터베이스에서 유저 정보를 찾습니다
     * @param email (user email)
     * @return memberEntity
     */
    Optional<MemberEntity> findByEmail(String email);
}
