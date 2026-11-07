# Cantina — Builder + Command

Projeto final de Design Patterns: sistema de cantina em Java 25 + Spring Boot. O atendente monta o pedido passo a passo (**Builder**) e opera o balcão com ações que podem ser desfeitas (**Command**).

> **README provisório.** A versão final (descrição, exemplos de uso, patterns, declaração de IA e contribuições) é montada pela Frente D. O plano completo da Parte 1 está em [docs/PLANO.md](docs/PLANO.md).

## Pré-requisitos

- JDK 25
- Maven não precisa: vem pelo wrapper (`./mvnw`)

## Como rodar

```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
./mvnw test                 # testes
```

- Swagger: http://localhost:8080/swagger-ui.html

## Estado atual

Esqueleto: todas as classes do contrato (seção 7 do plano) existem e compilam, com corpo `TODO`. Cada frente preenche a sua parte (seção 8 do plano).

## Declaração de IA (parcial)

- Plano da Parte 1 e esqueleto do projeto: gerados com auxílio de IA (Claude Opus 5.5, da Anthropic).
