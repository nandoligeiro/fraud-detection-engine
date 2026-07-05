# ADR-003 — Java 21 e Spring Boot 3 como Stack Principal

## Status

Aceita

## Contexto

O desafio é voltado para Engenharia Backend e exige uma solução escalável, segura, observável e de alta vazão.

A stack deve ser madura, conhecida em ambientes financeiros, compatível com Kafka, Redis, PostgreSQL, OpenTelemetry e Kubernetes.

## Decisão

Utilizar **Java 21 LTS** com **Spring Boot 3** como stack principal da solução.

## Justificativa

Java 21 e Spring Boot 3 oferecem:

- ecossistema maduro para microsserviços;
- integração sólida com Kafka, Redis, PostgreSQL e observabilidade;
- suporte corporativo amplo;
- alta produtividade;
- compatibilidade com boas práticas de Clean Architecture e Arquitetura Hexagonal;
- Virtual Threads para workloads bloqueantes quando aplicável;
- bom ferramental para testes com JUnit, Testcontainers e WireMock.

## Trade-offs

### Vantagens

- Stack amplamente utilizada em bancos.
- Facilidade de contratação e manutenção.
- Excelente integração com ferramentas corporativas.
- Grande comunidade.
- Boa produtividade.

### Desvantagens

- Maior consumo de memória quando comparado a Go ou Rust.
- Tempo de startup maior, embora mitigável.
- Necessidade de atenção a GC, pool de conexões e tuning da JVM.

## Consequências

A solução fica alinhada ao ecossistema corporativo e ao perfil esperado para backend sênior Java, mantendo capacidade de escalar horizontalmente em Kubernetes.

## Observação sobre Virtual Threads

Virtual Threads não são a solução mágica para todo tipo de carga. Elas ajudam principalmente em operações bloqueantes, como chamadas REST ou banco de dados.

Para consumidores Kafka e processamento CPU-bound, o dimensionamento continuará sendo feito por particionamento, consumer groups, controle de concorrência e tuning da aplicação.
