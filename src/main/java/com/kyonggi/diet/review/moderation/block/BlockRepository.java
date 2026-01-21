package com.kyonggi.diet.review.moderation.block;

import com.kyonggi.diet.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    // 차단한 멤버 목록
    @Query("select mb.blocked.id from Block mb where mb.blocker = :blocker")
    List<Long> findBlockedMemberIds(MemberEntity blocker);

    boolean existsByBlockerAndBlocked(MemberEntity blocker, MemberEntity blocked);
}
