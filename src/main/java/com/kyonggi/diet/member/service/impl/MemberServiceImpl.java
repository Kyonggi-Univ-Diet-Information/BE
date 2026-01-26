package com.kyonggi.diet.member.service.impl;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.auth.socialAccount.SocialAccount;
import com.kyonggi.diet.auth.socialAccount.SocialAccountRepository;
import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import com.kyonggi.diet.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private final SocialAccountRepository socialAccountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;

    @Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        log.info("멤버 정보 Member DTO {}", memberDTO);
        //TODO: 중복된 계정 예외처리 추가
        //TODO: 비밀번호 인코드 과정 추가
        memberDTO.setPassword(encoder.encode(memberDTO.getPassword()));
        MemberEntity memberEntity = MemberEntity.builder()
                .email(memberDTO.getEmail())
                .password(memberDTO.getPassword())
                .name(memberDTO.getName())
                .profileUrl(memberDTO.getProfileUrl())
                .build();
        memberEntity = memberRepository.save(memberEntity);
        log.info("멤버 정보를 생성하였습니다 {}", memberEntity);
        return mapToMemberDTO(memberEntity);
    }

    /**
     * 모든 멤버 정보를 반환합니다
     * @return list
     */
    @Override
    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> list = memberRepository.findAll();
        log.info("List of members = {}", list);
        if (list.isEmpty()) {
            throw new EntityNotFoundException("멤버 정보를 찾을 수 없습니다.");
        }
        return list.stream().map(this::mapToMemberDTO).collect(Collectors.toList());
    }

    /**
     * 한 명의 멤버 정보를 반환합니다.
     * @param id (member id)
     * @return memberDTO
     */
    @Override
    public MemberDTO getMemberById(Long id) {
        Optional<MemberEntity> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            MemberEntity member = optionalMember.get();
            log.info("Founded member = {}", member);
            return mapToMemberDTO(member);
        }
        log.warn("멤버 정보를 찾을 수 없습니다. = {} ", id);
        throw new EntityNotFoundException("ID에 해당하는 멤버 정보를 찾을 수 없습니다.");
    }

    @Override
    public MemberEntity getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(()-> new NoSuchElementException("EMAIL 에 해당하는 멤버를 찾을 수 없습니다."));
    }

    @Override
    public String getNameById(Long id) {
        return memberRepository.findNameById(id);
    }

    /**
     * MemberEntity 에서 MemberDTO 으로 변환하는 Mapper Method 입니다.
     * @param memberEntity (member entity)
     * @return MemberDTO
     */
    private MemberDTO mapToMemberDTO(MemberEntity memberEntity) {
        return modelMapper.map(memberEntity, MemberDTO.class);
    }

    /**
     * MemberDTO 에서 MemberEntity 으로 변환하는 Mapper Method 입니다.
     * @param memberDTO (member entity)
     * @return MemberEntity
     */
    private MemberEntity mapToMemberEntity(MemberDTO memberDTO) {
        return modelMapper.map(memberDTO, MemberEntity.class);
    }

    @Override
    public Provider getProvider(String email) {
        MemberEntity member = getMemberByEmail(email);
        SocialAccount socialAccount = socialAccountRepository.findByMemberId(member.getId()).orElseThrow(
                () -> new NoSuchElementException("해당 멤버에 대한 소셜 어카운트를 획들학 수 없습니다."));
        return socialAccount.getProvider();
    }
}
