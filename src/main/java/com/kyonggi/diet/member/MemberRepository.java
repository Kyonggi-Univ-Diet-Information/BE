package com.kyonggi.diet.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /**
     * 이메일을 통해 데이터베이스에서 유저 정보를 찾습니다
     * @param email (user email)
     * @return memberEntity
     */
    Optional<MemberEntity> findByEmail(String email);
    Optional<MemberEntity> findByAppleSub(String appleSub);

    @Query("select m.name from MemberEntity m where m.id = :id")
    String findNameById(@Param("id") Long id);

    Optional<MemberEntity> findByGoogleSub(String googleSub);
}
