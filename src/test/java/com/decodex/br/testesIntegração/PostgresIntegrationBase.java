package com.decodex.br.testesIntegração;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

@SuppressWarnings("resource")
public abstract class PostgresIntegrationBase {

    private static PostgreSQLContainer<?> POSTGRES = null;
    private static boolean usandoLocal = false;

    static {
        // Tenta verificar se Docker está disponível (opcional, evita exceção pesada)
        boolean dockerDisponivel;
        try {
            DockerClientFactory.instance().client();
            dockerDisponivel = true;
        } catch (Throwable t) {
            dockerDisponivel = false;
        }

        if (dockerDisponivel) {
            try {
                POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
                        .withDatabaseName("gestao_financeira_test")
                        .withUsername("test")
                        .withPassword("test")
                        .withReuse(true);
                POSTGRES.start();
                System.out.println(">>> [Testcontainers] PostgreSQL iniciado com sucesso!");
            } catch (Exception e) {
                System.err.println(">>> [Testcontainers] Falhou ao iniciar: " + e.getMessage());
                System.err.println(">>> Usando fallback para PostgreSQL local.");
                POSTGRES = null;
                usandoLocal = true;
            }
        } else {
            System.err.println(">>> Docker não disponível. Usando fallback para PostgreSQL local.");
            usandoLocal = true;
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (POSTGRES != null && POSTGRES.isRunning()) {
            // Configuração via Testcontainers
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        } else {
            // Fallback: usa banco local (supondo que exista e esteja rodando)
            System.out.println(">>> Usando configuração de banco local (definida em application-test.yml/properties)");
            // Não sobrescreve nada – o Spring usará as propriedades já carregadas
            // do arquivo application-test.yml ou application-test.properties
        }
    }

    // Opcional: método para saber se está rodando local
    public static boolean isUsandoLocal() {
        return usandoLocal;
    }
}