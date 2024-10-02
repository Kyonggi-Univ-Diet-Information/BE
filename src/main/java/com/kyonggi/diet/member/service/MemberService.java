package com.kyonggi.diet.member.service;

import com.kyonggi.diet.member.MemberDTO;

import java.util.List;

/**
 * 멤버 서비스 인터페이스
 * @author boroboro01
 */
public interface MemberService {

    /**
     * 데이터베이스에 멤버 정보를 저장합니다
     * @param memberDTO (member dto)
     * @return memberDTO
     */
    MemberDTO createMember(MemberDTO memberDTO);
    /**
     * 데이터베이스로 부터 멤버 데이터를 모두 가져옵니다
     * @return list of memberDTO
     */
    List<MemberDTO> getAllMembers();

    /**
     * 데이터베이스로 부터 해당하는 Id의 멤버 데이터를 가져옵니다
     * @return MemberDTO
     */
    MemberDTO getMemberById(Long id);
}
