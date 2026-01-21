package com.kyonggi.diet.review.moderation.block;

import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    public List<Long> getBlockedMemberIds(Long blockerId) {
        MemberEntity blocker = memberRepository.findById(blockerId).orElseThrow(
                () -> new NoSuchElementException("해당 id에 대한 멤버가 없습니다: " + blockerId));
        return blockRepository.findBlockedMemberIds(blocker);
    }

    @Transactional
    public void block(Long blockerId, Long blockedId) {
        MemberEntity blocker = memberRepository.findById(blockerId).orElseThrow();
        MemberEntity blocked = memberRepository.findById(blockedId).orElseThrow();
        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) return;
        blockRepository.save(
                Block.builder()
                        .blocker(blocker)
                        .blocked(blocked).build()
        );
    }
}
