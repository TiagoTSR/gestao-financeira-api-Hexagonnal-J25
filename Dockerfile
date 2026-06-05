# ETAPA 1: Construção (Build)
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
# Copia apenas arquivos de configuração do Maven para baixar dependências
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Garante permissão de execução no mvnw
RUN chmod +x mvnw

# Baixa as dependências (isso será cacheado)
RUN ./mvnw dependency:go-offline
# Copia o código fonte e compila
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ETAPA 2: Execução (Run)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
# Copia apenas o .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que o Spring Boot usa
EXPOSE 8080
# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]