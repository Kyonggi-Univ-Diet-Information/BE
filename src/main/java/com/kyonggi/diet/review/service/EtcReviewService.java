package com.kyonggi.diet.review.service;

import com.kyonggi.diet.member.CustomUserDetails;
import com.kyonggi.diet.review.DTO.RecentReviewDTO;
import com.kyonggi.diet.review.moderation.block.BlockService;
import com.kyonggi.diet.review.repository.EtcReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtcReviewService {

    private final EtcReviewRepository etcReviewRepository;
    private final BlockService blockService;

    public List<RecentReviewDTO> getRecentTop5(CustomUserDetails user) {

        // 비로그인
        if (user == null) {
            return etcReviewRepository.findRecent5Reviews();
        }

        // 로그인
        List<Long> blockedIds =
                blockService.getBlockedMemberIds(user.getMemberId());

        if (blockedIds.isEmpty()) {
            return etcReviewRepository.findRecent5Reviews();
        }

        return etcReviewRepository
                .findRecent5ReviewsExcludeBlocked(blockedIds);
    }
}