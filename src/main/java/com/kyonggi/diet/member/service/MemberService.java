package com.kyonggi.diet.member.service;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.member.MemberEntity;

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

    MemberEntity findOrCreateAppleMember(String appleSub, String email, String name);
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

    /**
     * 데이터베이스로 부터 해당하는 email의 멤버 엔티티를 가져옵니다.
     * @param email (String)
     * @return MemberEntity
     */
    MemberEntity getMemberByEmail(String email);

    String getNameById(Long id);

    Provider getProvider(String email);
}
