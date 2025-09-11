package com.kyonggi.diet.translation.service;

import com.kyonggi.diet.translation.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final OpenAiClient openAiClient; // 직접 구현하거나 WebClient 사용

    public String translateToEnglish(String koreanFood) {
        String prompt = "Translate the following Korean food name into natural English. "
                + "Output only the dish name as a single phrase. "
                + "Do not add explanations, sentences, or extra words.\n\n"
                + "Korean: " + koreanFood + "\nEnglish:";

        try {
            String result = openAiClient.translate(prompt);

            // 혹시라도 따옴표나 줄바꿈이 섞일 수 있으니 trim
            return result.trim().replaceAll("^\"|\"$", "");
        } catch (Exception e) {
            e.printStackTrace();
            return koreanFood; // 실패 시 원래 한글명 리턴
        }
    }
}
