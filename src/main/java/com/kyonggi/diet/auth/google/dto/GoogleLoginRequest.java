package com.kyonggi.diet.auth.google.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class GoogleLoginRequest {

    @NotBlank(message = "authorization code는 필수입니다.")
    private String code;
}
