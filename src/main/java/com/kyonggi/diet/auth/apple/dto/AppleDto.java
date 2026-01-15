package com.kyonggi.diet.auth.apple.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppleDto {
    private String sub; //apple sub
    private String token; //access token
    private String email;
}