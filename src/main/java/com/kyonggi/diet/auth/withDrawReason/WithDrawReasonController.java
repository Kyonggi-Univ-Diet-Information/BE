package com.kyonggi.diet.auth.withDrawReason;

import com.kyonggi.diet.auth.withDrawReason.domain.WithDrawReasonType;
import com.kyonggi.diet.auth.withDrawReason.dto.WithDrawReasonDto;
import com.kyonggi.diet.auth.withDrawReason.dto.WithDrawReasonEtcRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/withdraw")
@Slf4j
@Tag(
    name = "탈퇴 사유 설문 API",
    description = "회원 탈퇴 사유 설문 및 통계 API"
)
public class WithDrawReasonController {

    private final WithDrawReasonService withDrawReasonService;

    @Operation(
        summary = "탈퇴 사유 목록 조회",
        description = "회원 탈퇴 시 선택할 수 있는 모든 탈퇴 사유(enum)와 설명을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "탈퇴 사유 목록 조회 성공")
    })
    @GetMapping("/reasons")
    public ResponseEntity<?> getWithdrawReasons() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", withDrawReasonService.getAllReasonTypes());
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "탈퇴 사유 타입별 카운트 조회",
        description = "모든 탈퇴 사유 타입(enum)에 대해 탈퇴 건수를 조회합니다. 데이터가 없는 타입은 0으로 반환됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "탈퇴 사유 타입별 통계 조회 성공")
    })
    @GetMapping("/each-count")
    public ResponseEntity<?> getEachCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", withDrawReasonService.getWithDrawReasonCountByEachType());
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "기타(ETC) 탈퇴 사유 상세 조회",
        description = "기타(ETC)로 입력된 모든 탈퇴 사유 텍스트를 조회합니다. (데이터 분석용)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기타 탈퇴 사유 조회 성공")
    })
    @GetMapping("/etcs")
    public ResponseEntity<?> getEtcs() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", withDrawReasonService.getEtcReasons());
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "탈퇴 사유 등록",
        description = """
            회원 탈퇴 시 선택한 탈퇴 사유를 저장합니다.
            
            - 로그인된 사용자만 호출 가능합니다.
            - 탈퇴 사유가 ETC인 경우, 기타 사유 텍스트 입력은 필수입니다.
            - 기타 사유 필수인 부분은 프론트에서 막아주세요.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 사유 저장 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/create/{type}")
    public ResponseEntity<?> createWithDrawReason(
            @PathVariable("type") WithDrawReasonType type,
            @RequestBody(required = false) WithDrawReasonEtcRequest reason
    ) {
        try {
            // ETC가 아닌데 기타 사유가 들어온 경우 차단
            if (type != WithDrawReasonType.ETC
                    && reason != null
                    && reason.getEtcReason() != null) {
                return ResponseEntity.badRequest()
                        .body("ETC가 아닌 탈퇴 사유에는 기타 사유를 입력할 수 없습니다.");
            }

            WithDrawReasonDto dto = WithDrawReasonDto.builder()
                    .type(type)
                    .build();

            if (reason != null) {
                dto.setEtcReason(reason.getEtcReason());
            }

            withDrawReasonService.save(dto);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
