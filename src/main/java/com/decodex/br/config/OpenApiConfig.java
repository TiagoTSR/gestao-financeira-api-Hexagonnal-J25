package com.decodex.br.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestão Financeira API")
                        .version("v0.0.1")
                        .description("API para controle financeiro pessoal, desenvolvida com arquitetura hexagonal no Java 25.")
                        .contact(new Contact()
                                .name("Suporte")
                                .email("suporte@decodex.com")));
    }
}
