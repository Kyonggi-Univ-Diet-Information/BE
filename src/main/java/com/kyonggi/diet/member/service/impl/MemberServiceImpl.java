package com.kyonggi.diet.member.service.impl;

import com.kyonggi.diet.auth.Provider;
import com.kyonggi.diet.auth.socialAccount.SocialAccount;
import com.kyonggi.diet.auth.socialAccount.SocialAccountRepository;
import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.DTO.NicknameCheckResponse;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.member.service.NicknameFilterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;
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
    private final NicknameFilterService nicknameFilterService;

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣 ]{2,15}$");

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

    private void validateNicknameFormat(String nickname) {
        if (nickname == null) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new IllegalArgumentException("닉네임 형식이 올바르지 않습니다. (한글, 영어, 숫자, 공백을 포함한 2자 이상 15자 이하)");
        }
    }

    private void validateNicknamePolicy(String nickname) {

        if (nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        String noSpace = nickname.replaceAll("\\s+", "");

        if (noSpace.matches("^[ㄱ-ㅎ]+$") || noSpace.matches("^[ㅏ-ㅣ]+$")) {
            throw new IllegalArgumentException("자음 또는 모음만으로는 닉네임을 만들 수 없습니다.");
        }
    }

    private void validateNicknameBanned(String nickname) {
        if (nicknameFilterService.isBanned(nickname)) {
            throw new IllegalArgumentException("사용할 수 없는 단어가 포함된 닉네임입니다.");
        }
    }

    @Override
    @Transactional
    public String updateMemberNickname(String email, String nickname) {

        MemberEntity member = getMemberByEmail(email);

        validateNicknameFormat(nickname);
        validateNicknameBanned(nickname);
        validateNicknamePolicy(nickname);

        nickname = nickname.toLowerCase();

        if (member.getNickname().equals(nickname)) {
            throw new IllegalArgumentException("기존 닉네임과 동일합니다.");
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        member.updateNickname(nickname);
        member.updateNicknameUpdatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        return nickname;
    }

    @Override
    public NicknameCheckResponse checkNicknameAvailable(String nickname) {

        try {
            validateNicknameFormat(nickname);
            validateNicknamePolicy(nickname);
            validateNicknameBanned(nickname);

            boolean exists = memberRepository.existsByNickname(nickname.toLowerCase());

            if (exists) {
                return new NicknameCheckResponse(false, "이미 존재하는 닉네임입니다.");
            }

            return new NicknameCheckResponse(true, "사용 가능한 닉네임입니다.");

        } catch (IllegalArgumentException e) {
            return new NicknameCheckResponse(false, e.getMessage());
        }
    }
}
