# ADR-006 — Observabilidade como Requisito de Primeira Classe

## Status

Aceita

## Contexto

O motor antifraude opera em um fluxo crítico, com alto volume, baixa latência e impacto direto na experiência do cliente e na operação antifraude.

Sem observabilidade, a equipe não conseguiria responder rapidamente a incidentes como:

- aumento de consumer lag;
- queda de throughput;
- aumento de falsos positivos;
- indisponibilidade de Redis, Kafka ou serviços externos;
- degradação de latência no motor;
- falha no envio de notificações.

## Decisão

Tratar observabilidade como parte da arquitetura, não como pós-implementação.

A solução usará:

- **OpenTelemetry** para tracing distribuído;
- **Micrometer** para métricas de aplicação;
- **Actuator** para health checks e exposição operacional;
- logs estruturados em JSON;
- dashboards e alertas baseados em SLO/SLI.

## Justificativa

O sistema precisa ser operável por times de SRE e engenharia.

A observabilidade deve permitir responder rapidamente:

- o sistema está saudável?
- qual é a vazão atual?
- onde está o gargalo?
- qual regra gerou o alerta?
- qual dependência está degradada?
- houve aumento de falso positivo?
- existe atraso de processamento?

## Sinais principais

### Métricas

- `transactions_received_total`
- `transactions_processed_total`
- `fraud_alerts_total`
- `rules_triggered_total`
- `rule_execution_time_seconds`
- `kafka_consumer_lag`
- `redis_latency_seconds`
- `notification_latency_seconds`
- `idempotency_duplicate_total`

### Logs

Todos os logs relevantes devem conter:

- `traceId`
- `spanId`
- `transactionId`
- `ruleId`
- `ruleVersion`
- `alertId`
- `decision`
- `processingTimeMs`

### Tracing

O trace deve acompanhar a jornada:

```text
API Gateway
→ Ingestion Service
→ Kafka transaction-events
→ Fraud Engine
→ Rule Engine
→ Kafka fraud-alerts
→ Notification Service
```

## Consequências positivas

- Diagnóstico mais rápido.
- Melhor resposta a incidentes.
- Capacidade de explicar decisões.
- Base para SLOs e alertas proativos.
- Melhor visibilidade de gargalos.

## Consequências negativas

- Aumento do volume de dados de telemetria.
- Custo adicional de armazenamento e processamento.
- Necessidade de padronização de logs e métricas.

## Mitigações

- Amostragem configurável de traces.
- Logs sem dados sensíveis.
- Métricas agregadas.
- Retenção ajustada por criticidade.
- Naming convention para métricas e atributos.
