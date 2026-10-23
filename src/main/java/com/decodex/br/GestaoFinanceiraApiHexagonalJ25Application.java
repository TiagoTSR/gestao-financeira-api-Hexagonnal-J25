package com.decodex.br;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.io.File;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class GestaoFinanceiraApiHexagonalJ25Application {

	public static void main(String[] args) {

		Dotenv dotenv;
		if (new File(".env").exists()) {
			dotenv = Dotenv.configure().load();
		} else if (new File("gestao-financeira-api-Hexagonnal-J25/.env").exists()) {
			dotenv = Dotenv.configure().directory("gestao-financeira-api-Hexagonnal-J25").load();
		} else {
			dotenv = Dotenv.configure().ignoreIfMissing().load();
		}

		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Suporte automático para Railway (quando DATABASE_URL é injetada)
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl != null && !databaseUrl.isBlank()) {
			if (!databaseUrl.startsWith("jdbc:")) {
				databaseUrl = "jdbc:" + databaseUrl;
			}
			System.setProperty("DB_URL2", databaseUrl);
		}

		String pgUser = System.getenv("PGUSER");
		if (pgUser != null && !pgUser.isBlank() && System.getProperty("DB_USER1") == null) {
			System.setProperty("DB_USER1", pgUser);
		}

		String pgPassword = System.getenv("PGPASSWORD");
		if (pgPassword != null && !pgPassword.isBlank() && System.getProperty("DB_PASSWORD1") == null) {
			System.setProperty("DB_PASSWORD1", pgPassword);
		}

		SpringApplication.run(GestaoFinanceiraApiHexagonalJ25Application.class, args);
	}

}