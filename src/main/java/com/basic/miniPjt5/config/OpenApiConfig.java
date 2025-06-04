package com.basic.miniPjt5.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("MiniPjt5 API")
                        .description("JWT 인증이 적용된 API 문서입니다.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, createBearerScheme()))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
