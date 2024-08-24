package com.kyonggi.diet.member.service.impl;

import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 멤버 서비스 구현
 * @author boroboro01
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    /**
     * 모든 멤버 정보를 반환합니다
     * @return list
     */
    @Override
    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> list = memberRepository.findAll();
        log.info("List of members = {}", list);
        List<MemberDTO> listOfMembers = list.stream().map(memberEntity -> mapToMemberDTO(memberEntity)).collect(Collectors.toList());
        return listOfMembers;
    }

    /**
     * MemberEntity 에서 MemberDTO 으로 변환하는 Mapper Method 입니다.
     * @param memberEntity (member entity)
     * @return MemberDTO
     */
    private MemberDTO mapToMemberDTO(MemberEntity memberEntity) {
        return modelMapper.map(memberEntity, MemberDTO.class);
    }
}
