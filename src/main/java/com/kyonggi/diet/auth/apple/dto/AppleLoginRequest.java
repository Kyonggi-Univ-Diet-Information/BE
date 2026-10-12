package com.kyonggi.diet.auth.apple.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleLoginRequest {

    private String code;
    private String state;
    private AppleUser user;
    private String platform;

    public boolean isIosApp() {
        return "ios".equals(platform);
    }

    @Getter
    public static class AppleUser {
        private Name name;
        private String email;

        @Getter
        public static class Name {
            private String firstName;
            private String lastName;
        }
    }
}