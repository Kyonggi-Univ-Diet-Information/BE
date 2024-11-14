package com.kyonggi.diet.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyonggi.diet.member.MemberDTO;
import com.kyonggi.diet.member.MemberEntity;
import com.kyonggi.diet.member.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
            sb.append("&client_id=");  //발급받은 key
            sb.append("&redirect_uri=");     // 본인이 설정해 놓은 redirect_uri 주소
            sb.append("&code=").append(authorizeCode);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode + "확인");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result + "결과");

            JsonParser parser = new com.google.gson.JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + accessToken);
            System.out.println("refresh_token : " + refreshToken);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getUserInfo(String accessToken, HttpSession session, RedirectAttributes rttr) {
        HashMap<String, Object> userInfo = new HashMap<>();
        log.info("Called getUserInfo()");

        String requestURL = "https://kapi.kakao.com/v2/user/me";
        String view = null;
        String msg = null;

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode(); //서버에서 보낸 http 상태코드 반환
            System.out.println("responseCode :" + responseCode + "여긴가");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // 버퍼를 사용하여 읽은 것
            String line = "";
            String result = "";
            while ((line = buffer.readLine()) != null) {
                result += line;
            }

            System.out.println("response body :" + result);

            // 읽었으니깐 데이터꺼내오기
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result); //Json element 문자열변경
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            //userInfo에 사용자 정보 저장
            userInfo.put("email", email);
            userInfo.put("nickname", nickname);

            log.info(String.valueOf(userInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 이메일로 회원을 조회합니다.
        Optional<MemberEntity> memberOpt = memberRepository.findByEmail((String) userInfo.get("email"));

        MemberEntity member;
        if (memberOpt.isEmpty()) {
            // 회원이 없다면 새로 저장
            member = MemberEntity.builder()
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("nickname"))
                    .build();
            memberRepository.save(member);
            session.setAttribute("member", member);
            view = "redirect:/";
            msg = "회원가입 성공 및 로그인 완료";
        } else {
            // 기존 회원이라면 바로 로그인 처리
            member = memberOpt.get();
            session.setAttribute("member", member);
            view = "redirect:/";
            msg = "로그인 성공";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }
}
