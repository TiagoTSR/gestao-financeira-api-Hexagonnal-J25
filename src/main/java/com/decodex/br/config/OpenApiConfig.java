package com.decodex.br.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Gestão Financeira API")
                        .version("v0.0.1")
                        .description("API para controle financeiro pessoal, desenvolvida com arquitetura hexagonal no Java 25.")
                        .contact(new Contact()
                                .name("Suporte")
                                .email("suporte@decodex.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
