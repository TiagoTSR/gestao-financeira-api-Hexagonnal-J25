package com.decodex.br.testesIntegração;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

// 1. REMOVA as anotações @Testcontainers e @Container
public abstract class PostgresIntegrationBase {

    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("gestao_financeira_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    // 2. ADICIONE este bloco estático. Ele garante que o container inicie 
    // apenas UMA VEZ por rodada de testes, e fique vivo para todas as classes.
    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // (Se você optou por manter o Flyway ativado, deixe estas 3 linhas abaixo)
        // registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        // registry.add("spring.flyway.user", POSTGRES::getUsername);
        // registry.add("spring.flyway.password", POSTGRES::getPassword);
    }
}