# ADR-001 — Arquitetura Orientada a Eventos

## Status

Aceita

## Contexto

O motor antifraude precisa processar milhares de transações por segundo, suportar picos de carga, desacoplar sistemas internos e gerar alertas em tempo real sem bloquear o fluxo principal de transações.

Uma abordagem puramente síncrona via REST aumentaria acoplamento, reduziria resiliência e dificultaria replay de eventos em caso de falhas.

## Decisão

Adotar **Event-Driven Architecture** como estilo arquitetural principal.

As transações serão publicadas como eventos imutáveis em Kafka, consumidas pelo Fraud Engine e transformadas em alertas quando regras forem acionadas.

## Consequências positivas

- Desacoplamento entre produtores e consumidores.
- Melhor absorção de picos de carga.
- Possibilidade de replay.
- Escalabilidade por consumer groups.
- Integração flexível com sistemas internos.
- Menor impacto de falhas em serviços auxiliares.

## Consequências negativas

- Aumento da complexidade operacional.
- Necessidade de monitorar consumer lag.
- Necessidade de idempotência.
- Debug distribuído mais complexo.

## Mitigações

- Observabilidade com tracing distribuído.
- DLQ para falhas não recuperáveis.
- Idempotência por `transactionId`.
- Dashboards de lag, throughput, erro e latência.
