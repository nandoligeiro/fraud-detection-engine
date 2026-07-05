# ADR-004 — Idempotência e Effectively-Once Processing

## Status

Aceita

## Contexto

Em sistemas distribuídos, retentativas, duplicidades e reentregas são esperadas.

Kafka, APIs REST e serviços externos normalmente operam com semântica **at-least-once**. Tentar prometer exatamente uma execução física em todos os pontos do fluxo aumentaria complexidade e ainda assim não eliminaria todos os cenários de duplicidade.

## Decisão

Assumir entrega **at-least-once** e garantir **effectively-once processing** por idempotência na camada de aplicação.

A chave principal será `transactionId`.

## Estratégia

- `Ingestion Service` verifica duplicidade antes de publicar evento.
- `Fraud Engine` verifica se a decisão para `transactionId` já foi produzida.
- `Notification Service` verifica se o alerta para `transactionId`/`alertId` já foi enviado.
- Redis será usado para operações rápidas com TTL.
- Auditoria persistente será usada para rastreabilidade.

## Exemplo

```text
SET idempotency:transaction:{transactionId} PROCESSING NX EX 300
```

Se a chave já existir, o evento é tratado como duplicado.

## Consequências positivas

- Evita alertas duplicados.
- Reduz impacto de retentativas.
- Simplifica o modelo mental.
- Mantém compatibilidade com Kafka e REST.

## Consequências negativas

- Redis se torna dependência relevante.
- É preciso definir TTLs adequados.
- Casos de falha parcial exigem reconciliação.

## Mitigações

- Auditoria persistente.
- DLQ para eventos inconsistentes.
- Métricas de duplicidade.
- Reprocessamento controlado.
- Chaves separadas para ingestão, decisão e notificação.
