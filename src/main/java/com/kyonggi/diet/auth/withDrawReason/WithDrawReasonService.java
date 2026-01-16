package com.kyonggi.diet.auth.withDrawReason;

import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReason;
import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReasonType;
import com.kyonggi.diet.auth.withDrawReason.dto.WithDrawReasonDto;
import com.kyonggi.diet.auth.withDrawReason.dto.WithDrawReasonTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithDrawReasonService {

    private final WithDrawReasonRepository withDrawReasonRepository;

    /**
     * 탈퇴 사유 등록
     */
    @Transactional
    public Long save(WithDrawReasonDto dto) {
        if (dto.getType() == WithDrawReasonType.ETC &&
                (dto.getEtcReason() == null || dto.getEtcReason().isBlank())) {
            throw new IllegalArgumentException("기타 사유는 내용을 입력해야 합니다.");
        }
        WithDrawReason entity = WithDrawReason.builder()
                .type(dto.getType())
                .etcReason(dto.getEtcReason())
                .build();

        WithDrawReason saved = withDrawReasonRepository.save(entity);
        return saved.getId();
    }

    /**
     * 탈퇴 사유 enum 목록 조회
     */
    public List<WithDrawReasonTypeResponse> getAllReasonTypes() {
        return Arrays.stream(WithDrawReasonType.values())
                .map(type -> new WithDrawReasonTypeResponse(
                        type.name(),
                        type.getDescription()
                ))
                .toList();
    }

    /**
     * 각 탈퇴 사유별 카운팅
     */
    public Map<String, Long> getWithDrawReasonCountByEachType() {

        Map<WithDrawReasonType, Long> result = new EnumMap<>(WithDrawReasonType.class);
        for (WithDrawReasonType type : WithDrawReasonType.values()) {
            result.put(type, 0L);
        }

        List<Object[]> counts = withDrawReasonRepository.countGroupByType();
        for (Object[] row : counts) {
            WithDrawReasonType type = (WithDrawReasonType) row[0];
            Long count = (Long) row[1];
            result.put(type, count);
        }

        return result.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue
                ));
    }


    /**
     * 탈퇴 사유가 기타인 내용 리스트 출력
     */
    public List<String> getEtcReasons() {
        return withDrawReasonRepository.findByType(WithDrawReasonType.ETC)
                .stream()
                .map(WithDrawReason::getEtcReason)
                .toList();
    }
}
