# Estágio 1: Build com Maven e JDK 21 (LTS)
FROM maven:3.9.8-eclipse-temurin-21-jammy AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# -----------------------------------------------------

# Estágio 2: Imagem final, leve, com JRE 21 (LTS) para rodar
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# ---- CORREÇÃO 1: Copiamos o arquivo com seu nome exato para a pasta atual (.) ----
COPY --from=builder /app/target/web-0.0.1-SNAPSHOT.jar .

# Expõe a porta que sua API usa
EXPOSE 8080

# ---- CORREÇÃO 2: Executamos o arquivo com seu nome exato ----
ENTRYPOINT ["java","-jar","web-0.0.1-SNAPSHOT.jar"]