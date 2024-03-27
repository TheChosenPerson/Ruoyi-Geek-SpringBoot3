package com.ruoyi.web.core.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI springShopOpenAPI() {
                return new OpenAPI()
                                .info(new Info().title("RuoYi Geek")
                                                .description("RuoYi Geek API文档")
                                                .version("v1")
                                                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("外部文档")
                                                .url("https://springshop.wiki.github.org/docs"));
        }

        @Bean
        public GroupedOpenApi sysApi() {
                return GroupedOpenApi.builder()
                                .group("sys系统模块")
                                .packagesToScan("com.ruoyi.web.controller.system")
                                .build();
        }

        @Bean
        public GroupedOpenApi commonApi() {
                return GroupedOpenApi.builder()
                                .group("基础模块")
                                .packagesToScan("com.ruoyi.web.controller.common")
                                .build();
        }

        @Bean
        public GroupedOpenApi payApi() {
                return GroupedOpenApi.builder()
                                .group("支付模块")
                                .pathsToMatch("/pay/**")
                                .build();
        }

}
