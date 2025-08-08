package com.example.hanaro.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        Server devServer = new Server();
        devServer.setUrl("/");

        Server prodServer = new Server();
        prodServer.setUrl("운영 URL");

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .servers(List.of(devServer, prodServer));
    }

    private Info apiInfo() {
        return new Info()
                .title("Hanaro 쇼핑몰 프로젝트 API") // API 문서 제목
                .description("하나은행 Digital hana 路 백엔드 실습 프로젝트 API 명세서입니다.") // API 문서 설명
                .version("1.0.0"); // API 버전
    }

    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathitem ->
                pathitem.readOperations().forEach(operation -> {
                    Parameter acceptlanguage = new Parameter()
                            .in(ParameterIn.HEADER.toString()) // 헤더로 지정
                            .name("Accept-Language")
                            .required(false)
                            .schema(new StringSchema()._default("ko")) // Ez "ko"
                            .description("언어 설정 (예: ko en en-Us)");
                    operation.addParametersItem(acceptlanguage);
                }));
    }

    @Bean
    public GroupedOpenApi nonApiGroup(OpenApiCustomizer globalHeaderCustomizer) {
        return GroupedOpenApi.builder()
                .group("non-api")
                .pathsToExclude("/api/**")
                .addOpenApiCustomizer(globalHeaderCustomizer)
                .build();
    }

    @Bean
    public GroupedOpenApi generalApi(OpenApiCustomizer globalHeaderCustomizer) {
        return GroupedOpenApi.builder()
                .group("general-api")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(globalHeaderCustomizer)
                .build();
    }
}
