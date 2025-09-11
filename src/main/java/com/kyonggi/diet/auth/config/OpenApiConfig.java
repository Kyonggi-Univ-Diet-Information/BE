package com.kyonggi.diet.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "https://api.kiryong.kr", description = "Production API"),
//                @Server(url = "http://localhost:8080", description = "Dev API") // dev
        }
)
@Configuration
public class OpenApiConfig {}