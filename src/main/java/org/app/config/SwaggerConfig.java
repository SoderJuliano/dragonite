package org.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("org.app")
                .displayName("DRAGONITE API")
                .pathsToMatch("/**")
                .build();
    }

//     @Bean
//     public OpenAPI openApi() {
//         return new OpenAPI()
//                 .info(new Info().title("DRAGONITE API BETA")
//                 .license(new License().name("Dragonite API")))
//                 .components(new Components()
//                         .addSecuritySchemes("Authorization",
//                                 new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("String")
//                                         .in(SecurityScheme.In.HEADER).name("Authorization")))
//                 .addSecurityItem(
//                         new SecurityRequirement().addList("Authorization", Arrays.asList("read", "write")));

//     }
        @Bean
        public OpenAPI openApi(@Value("${swagger.base-url:http://localhost:8080}") String baseUrl) {
        return new OpenAPI()
                .info(new Info().title("DRAGONITE API BETA")
                .license(new License().name("Dragonite API")))
                .servers(Arrays.asList(new Server().url(baseUrl)))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("String")
                                        .in(SecurityScheme.In.HEADER).name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization", Arrays.asList("read", "write")));
        }

}