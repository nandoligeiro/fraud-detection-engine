# Fraud Detection Engine

Motor de Detecção de Transações Suspeitas em Tempo Real.

Este repositório documenta e estrutura uma proposta técnica para um motor antifraude determinístico, orientado a eventos, construído com **Java 21**, **Spring Boot 3**, **Kafka**, **Redis**, **PostgreSQL**, **OpenTelemetry** e **Kubernetes**.

## Objetivo

Processar eventos de transações financeiras em tempo real, aplicar regras determinísticas extensíveis e gerar alertas internos e externos em até **500 ms** após o recebimento do evento.

## Princípios

- Event-Driven Architecture
- Java 21 + Spring Boot 3
- Rules Engine determinístico na primeira versão
- At-least-once delivery com efeito effectively-once via idempotência
- Observabilidade by design
- Segurança ponta a ponta com mTLS, criptografia e pseudonimização
- Escalabilidade horizontal
- Degradação controlada em caso de falhas

## Estrutura

```text
fraud-detection-engine/
├── docs/
│   ├── 01-PRD.md
│   ├── 02-Architecture.md
│   ├── 03-API.md
│   ├── 04-Security.md
│   ├── 05-Observability.md
│   ├── 06-Scalability.md
│   ├── 07-TestStrategy.md
│   ├── 08-Deployment.md
│   ├── 09-Roadmap.md
│   ├── 10-CodeStructure.md
│   └── adr/
├── diagrams/
├── openapi/
├── bruno/
├── k6/
├── docker/
├── helm/
├── src/
└── presentation/
```

## Estrutura de código

```text
br.com.nandoligeiro.frauddetection
├── domain
│   ├── model
│   └── service
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
└── infrastructure
    ├── adapter
    │   ├── in
    │   ├── out
    │   └── kafka
    └── config
```

A regra é simples: `domain` não conhece Spring, Kafka, Redis ou banco. `application` orquestra casos de uso por portas. `infrastructure` concentra adapters, DTOs externos, mappers de integração e configurações de framework.

Mais detalhes em [`docs/10-CodeStructure.md`](docs/10-CodeStructure.md).

## Decisões arquiteturais

As decisões estão registradas em `docs/adr`.

Principais decisões:

- Arquitetura orientada a eventos
- Kafka para desacoplamento e alta vazão
- Java 21 + Spring Boot 3 como stack principal
- Idempotência para garantir effectly-once
- Motor determinístico antes de IA
- Observabilidade e segurança como requisitos de primeira classe

## Como executar futuramente

Este kit começa como documentação e skeleton arquitetural. A implementação Spring Boot será evoluída incrementalmente.

```bash
make test
make run
make load-test
```

## Status

- [x] PRD
- [x] Architecture Design
- [x] ADRs
- [x] Diagramas Mermaid
- [x] OpenAPI inicial
- [x] Plano de testes
- [x] Roteiro de apresentação
- [ ] Implementação completa do motor
