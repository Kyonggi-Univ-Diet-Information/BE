package com.kyonggi.diet.review.favoriteReview.service;

import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 즐겨찾기 리뷰 공통 추상 클래스
 * - 멤버 조회, 공통 유효성 검사, ModelMapper 매핑 포함
 */
@Transactional(readOnly = true)
@NoArgsConstructor(force = true)
public abstract class AbstractFavoriteReviewService<T> {

    protected final MemberService memberService;
    protected final ModelMapper modelMapper;

    protected AbstractFavoriteReviewService(MemberService memberService, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    /** 좋아요 눌렀는지 확인 (하위 클래스에서 repository 호출로 구현) */
    protected abstract Long validateThisIsMine(MemberEntity member, Long reviewId);

    /** 공통 MemberEntity → MemberDTO 변환 */
    protected MemberDTO mapToMemberDTO(MemberEntity member) {
        return MemberDTO.builder()
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .password(member.getPassword())
                .profileUrl(member.getProfileUrl())
                .build();
    }
}
