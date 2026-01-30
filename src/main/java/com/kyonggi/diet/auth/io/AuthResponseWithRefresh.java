package com.kyonggi.diet.auth.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseWithRefresh {
    private String accessToken;
    private String refreshToken;
    private String email;
}
