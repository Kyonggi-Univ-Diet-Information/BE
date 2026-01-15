package com.kyonggi.diet.auth.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyonggi.diet.auth.io.AuthResponse;
import com.kyonggi.diet.auth.util.JwtTokenUtil;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import com.kyonggi.diet.member.service.CustomMembersDetailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomMembersDetailService membersDetailService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;


    public String getAccessToken(String authorizeCode) {
        String accessToken = "";
        String refreshToken = "";
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(requestUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(clientId);  // 발급받은 key
            sb.append("&redirect_uri=").append(redirectUri); // 설정된 redirect_uri
            sb.append("&code=").append(authorizeCode);

            // 로그 추가: 요청 바디 확인
            log.info("Request Body: {}", sb.toString());

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("Response Code from Kakao: {}", responseCode);

            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                // 카카오 API의 에러 메시지를 확인할 수 있도록 추가
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                log.error("Error Response from Kakao: {}", br.lines().toList());
                return null; // 에러 발생 시 null 반환
            }

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            // 로그 추가: 응답 본문 확인
            log.info("Response Body from Kakao: {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("Access Token: {}", accessToken);
            log.info("Refresh Token: {}", refreshToken);

            br.close();
            bw.close();
        } catch (IOException e) {
            log.error("Error while getting access token", e);
        }
        return accessToken;
    }

    public AuthResponse getUserInfo(String accessToken, HttpSession session, RedirectAttributes rttr) {
        HashMap<String, Object> userInfo = new HashMap<>();
        log.info("Called getUserInfo() with accessToken: {}", accessToken);

        String requestURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode(); // HTTP 상태코드
            log.info("Response Code from Kakao User Info: {}", responseCode);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = buffer.readLine()) != null) {
                result += line;
            }

            // 로그 추가: 응답 본문 확인
            log.info("Response Body from Kakao User Info: {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.get("nickname").getAsString();
            String email = kakao_account.get("email").getAsString();

            log.info("Parsed User Info - Email: {}, Nickname: {}", email, nickname);

            // 사용자 정보 저장
            userInfo.put("email", email);
            userInfo.put("nickname", nickname);
        } catch (Exception e) {
            log.error("Error while fetching user info", e);
        }

        // 이메일로 회원을 조회합니다.
        Optional<MemberEntity> memberOpt = memberRepository.findByEmail((String) userInfo.get("email"));

        MemberEntity member;
        String msg;

        if (memberOpt.isEmpty()) {
            // 회원이 없다면 새로 저장
            member = MemberEntity.builder()
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("nickname"))
                    .build();
            memberRepository.save(member);
            session.setAttribute("member", member);
            msg = "회원가입 성공 및 로그인 완료";
            log.info("New Member Registered: {}", member);
        } else {
            // 기존 회원이라면 바로 로그인 처리
            member = memberOpt.get();
            session.setAttribute("member", member);
            msg = "로그인 성공";
            log.info("Existing Member Logged In: {}", member);
        }
        rttr.addFlashAttribute("msg", msg);

        String jwtToken = jwtTokenUtil.generateToken(membersDetailService.loadUserByUsername(member.getEmail()));

        return new AuthResponse(jwtToken, member.getEmail());

    }

}
