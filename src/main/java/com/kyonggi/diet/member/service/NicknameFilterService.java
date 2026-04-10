package com.kyonggi.diet.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NicknameFilterService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "banned:nickname";
    private Set<String> bannedWords = new HashSet<>();
    private long lastLoadedAt = 0;
    private static final long TTL = 60 * 1000;

    private void refreshIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastLoadedAt > TTL) {
            Set<String> words = redisTemplate.opsForSet().members(KEY);
            bannedWords = (words != null) ? new HashSet<>(words) : new HashSet<>();
            lastLoadedAt = now;
        }
    }

    public boolean isBanned(String nickname) {
        refreshIfNeeded();

        String original = nickname.toLowerCase().replaceAll("\\s+", "");
        String normalized = normalize(nickname);
        String noNumbers = original.replaceAll("[0-9]", "");

        for (String banned : bannedWords) {
            String bannedLower = banned.toLowerCase().replaceAll("\\s+", "");
            String bannedNormalized = normalize(banned);
            String bannedNoNumbers = bannedLower.replaceAll("[0-9]", "");

            if (original.contains(bannedLower)) {
                return true;
            }
            if (!bannedNormalized.isEmpty() && normalized.contains(bannedNormalized)) {
                return true;
            }
            if (!bannedNormalized.isEmpty() && original.contains(bannedNormalized)) {
                return true;
            }
            if (normalized.contains(bannedLower)) {
                return true;
            }
            if (!noNumbers.isEmpty() && noNumbers.contains(bannedLower)) {
                return true;
            }
            if (!noNumbers.isEmpty() && !bannedNoNumbers.isEmpty() && noNumbers.contains(bannedNoNumbers)) {
                return true;
            }
        }

        return false;
    }

    private String normalize(String input) {
        if (input == null) return "";
        String result = input.toLowerCase();

        result = result
                .replace("1", "i")
                .replace("!", "i")
                .replace("0", "o")
                .replace("3", "e")
                .replace("5", "s")
                .replace("4", "a");

        if (input.matches(".*[가-힣].*")) {
            return result.replaceAll("[^가-힣]", "");
        }

        return result.replaceAll("[^a-z0-9]", "");
    }
}