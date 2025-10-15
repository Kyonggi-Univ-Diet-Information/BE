package com.kyonggi.diet.member.service.impl;

import com.kyonggi.diet.member.DTO.MemberDTO;
import com.kyonggi.diet.member.DTO.MyPageDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.MemberService;
import com.kyonggi.diet.review.DTO.ReviewDTO;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteDietFoodReviewRepository;
import com.kyonggi.diet.review.favoriteReview.repository.FavoriteKyongsulFoodReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    //private final DietFoodReviewService dietFoodReviewService;
    //private final KyongsulFoodReviewService kyongsulFoodReviewService;
    private final FavoriteDietFoodReviewRepository favoriteDietFoodReviewRepository;
    private final FavoriteKyongsulFoodReviewRepository favoriteKyongsulFoodReviewRepository;
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

    @Override
    public MyPageDTO getMyPage(String email) {
        MemberEntity member = getMemberByEmail(email);
        //List<ReviewDTO> dr = dietFoodReviewService.findAllByMember(member);
        //List<ReviewDTO> kr = kyongsulFoodReviewService.findAllByMember(member);
        List<ReviewDTO> fdr;
        List<ReviewDTO> fkr;

        return MyPageDTO.builder()
                .name(member.getName())
                .email(email)
                //.dietFoodReviews(dr)
                //.kyongsulReviews(kr)
                //.favoriteDietFoodReviews(fdr)
                //.favoriteKyongsulReviews(fkr)
                .build();
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
}
