package com.kyonggi.diet.review;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyonggi.diet.Food.domain.DietFood;
import com.kyonggi.diet.Food.repository.DietFoodRepository;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 리뷰 키워드 작성/수정/조회 통합 테스트 (H2, ci 프로필) */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("ci")
class ReviewKeywordApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired MemberRepository memberRepository;
    @Autowired DietFoodRepository dietFoodRepository;
    @Autowired JwtTokenUtil jwtTokenUtil;
    @Autowired CustomMembersDetailService detailService;

    private String bearer;
    private Long foodId;

    @BeforeEach
    void setUp() {
        String email = UUID.randomUUID() + "@test.com";
        memberRepository.save(MemberEntity.builder()
                .email(email).name("tester").nickname(UUID.randomUUID().toString().substring(0, 8)).build());
        bearer = "Bearer " + jwtTokenUtil.generateAccessToken(detailService.loadUserByUsername(email));
        foodId = dietFoodRepository.save(DietFood.builder().name("김치찌개").build()).getId();
    }

    private String body(String salt, String spicy, String portion, String value) throws Exception {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("rating", 4.5);
        m.put("title", "제목");
        m.put("content", "내용");
        m.put("saltLevel", salt);
        m.put("spicyLevel", spicy);
        m.put("portionLevel", portion);
        m.put("valueLevel", value);
        return om.writeValueAsString(m);
    }

    private JsonNode firstReview() throws Exception {
        String res = mvc.perform(get("/api/review/DORMITORY/paged/" + foodId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return om.readTree(res).get("content").get(0);
    }

    @Test
    void 키워드_없이_작성하면_400() throws Exception {
        mvc.perform(post("/api/review/DORMITORY/new/" + foodId)
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(null, null, null, null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 잘못된_enum_값이면_400() throws Exception {
        mvc.perform(post("/api/review/DORMITORY/new/" + foodId)
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("TOO_SALTY", null, null, null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 작성_조회_수정_해제_교체() throws Exception {
        // 작성: 간=HIGH, 양=LOW 만 선택
        mvc.perform(post("/api/review/DORMITORY/new/" + foodId)
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("HIGH", null, "LOW", null)))
                .andExpect(status().isOk());

        // 조회 (paged) 에 반영
        JsonNode r = firstReview();
        assertThat(r.get("saltLevel").asText()).isEqualTo("HIGH");
        assertThat(r.get("spicyLevel").isNull()).isTrue();
        assertThat(r.get("portionLevel").asText()).isEqualTo("LOW");
        assertThat(r.get("valueLevel").isNull()).isTrue();
        long reviewId = r.get("id").asLong();

        // 수정: 간 교체(HIGH→MID), 양 해제, 가성비 새로 선택
        mvc.perform(put("/api/review/DORMITORY/modify/" + reviewId)
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("MID", null, null, "HIGH")))
                .andExpect(status().isOk());

        // 조회 (one) 에 반영
        String one = mvc.perform(get("/api/review/DORMITORY/one/" + reviewId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode m = om.readTree(one);
        assertThat(m.get("saltLevel").asText()).isEqualTo("MID");
        assertThat(m.get("portionLevel").isNull()).isTrue();
        assertThat(m.get("valueLevel").asText()).isEqualTo("HIGH");

        // 수정: 전부 해제하면 400, 기존 값 유지
        mvc.perform(put("/api/review/DORMITORY/modify/" + reviewId)
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(null, null, null, null)))
                .andExpect(status().isBadRequest());
        assertThat(firstReview().get("saltLevel").asText()).isEqualTo("MID");
    }
}
