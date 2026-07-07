# Async Flow Logs

Este documento lista os logs esperados para validar o fluxo assíncrono do MVP.

## Objetivo

A resposta HTTP valida a ingestão. A continuidade do fluxo é observada por logs e pelo Kafka UI.

## Sequência esperada

```text
transaction ingestion started
transaction event published
transaction accepted for async processing
transaction event consumed
detection started
detection finished
```

Para transações suspeitas:

```text
alert created
fraud alert published
fraud alert delivered to notification simulator
```

## Campos relevantes

```text
transactionId
accountId
eventId
alertId
decision
severity
triggeredRules
topic
channel
country
```

## Como explicar

> No MVP, Bruno e k6 validam a entrada HTTP. Como a decisão é assíncrona, eu confirmo o restante da jornada pelos logs correlacionados por transactionId, eventId e alertId. Para produção, eu evoluiria esse desenho para OpenTelemetry, métricas de negócio e dashboards SRE.
