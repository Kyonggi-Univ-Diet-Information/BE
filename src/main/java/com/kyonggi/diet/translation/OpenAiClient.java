package com.kyonggi.diet.translation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.base-url}")
    private String baseUrl;

    public String translate(String prompt) {
        // OpenAI ChatCompletion 요청 JSON
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are a translator that translates Korean food names into natural English."),
                        Map.of("role", "user", "content", prompt)
                },
                "max_tokens", 50,
                "temperature", 0.3
        );

        try {
            // OpenAI API 호출
            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 응답에서 번역된 텍스트 꺼내기
            if (response != null && response.containsKey("choices")) {
                var choices = (java.util.List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString().trim();
                }
            }
            return prompt; // 실패 시 원래 텍스트 리턴
        } catch (Exception e) {
            e.printStackTrace();
            return prompt;
        }
    }
}
