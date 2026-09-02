package com.kyonggi.diet.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        // 상대경로로 두면 Swagger UI 가 문서를 연 호스트를 그대로 사용한다.
        servers = @Server(url = "/", description = "Current host")
)
@Configuration
public class OpenApiConfig {}