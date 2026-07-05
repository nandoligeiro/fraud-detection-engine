# Observability — Motor de Detecção de Transações Suspeitas

## 1. Objetivo

Definir como o sistema será monitorado, diagnosticado e operado em produção.

A observabilidade precisa permitir responder rapidamente:

- o motor está processando transações?
- existe atraso no Kafka?
- alguma dependência está degradada?
- qual regra está gerando mais alertas?
- houve aumento súbito de falsos positivos?
- o SLA de 500 ms está sendo respeitado?

## 2. Pilares

- Métricas
- Logs estruturados
- Tracing distribuído
- Health checks
- Alertas operacionais
- Dashboards de SRE

## 3. Métricas principais

### Throughput

- `transactions_received_total`
- `transactions_processed_total`
- `transactions_per_second`
- `fraud_alerts_total`

### Latência

- `transaction_end_to_end_latency_seconds`
- `rule_execution_time_seconds`
- `kafka_publish_latency_seconds`
- `redis_latency_seconds`
- `notification_latency_seconds`

### Kafka

- `kafka_consumer_lag`
- `kafka_records_consumed_total`
- `kafka_records_produced_total`
- `kafka_dlq_total`

### Regras

- `rules_loaded_total`
- `rules_triggered_total`
- `rule_errors_total`
- `rule_kill_switch_total`
- `false_positive_rate`

### Idempotência

- `idempotency_duplicate_total`
- `idempotency_store_error_total`

### Dependências

- `redis_errors_total`
- `postgres_errors_total`
- `notification_errors_total`

## 4. Logs estruturados

Formato recomendado: JSON.

Campos mínimos:

```json
{
  "timestamp": "2026-07-05T10:15:30.210Z",
  "level": "INFO",
  "service": "fraud-engine",
  "traceId": "trace-001",
  "spanId": "span-001",
  "transactionId": "tx-123456",
  "ruleId": "HIGH_AMOUNT",
  "ruleVersion": 3,
  "alertId": "alert-001",
  "decision": "SUSPICIOUS",
  "processingTimeMs": 83
}
```

Não registrar PII.

## 5. Tracing distribuído

A solução usa OpenTelemetry.

Fluxo esperado:

```text
API Gateway
→ Ingestion Service
→ Kafka transaction-events
→ Fraud Engine
→ Rule Engine
→ Kafka fraud-alerts
→ Notification Service
```

Atributos recomendados:

- `transaction.id`
- `rule.id`
- `rule.version`
- `alert.id`
- `fraud.decision`
- `kafka.topic`
- `kafka.partition`
- `kafka.offset`

## 6. Health checks

### Liveness

Verifica se a aplicação está viva.

### Readiness

Verifica se a aplicação consegue operar:

- Kafka disponível;
- Redis acessível;
- regras carregadas em memória;
- configuração válida.

## 7. SLOs iniciais

| SLO | Meta |
|---|---|
| Latência fim-a-fim | ≤ 500 ms |
| p95 interno do motor | < 100 ms |
| p99 interno do motor | < 200 ms |
| Disponibilidade | 99,95% |
| Alertas duplicados | 0 |
| Perda de eventos | 0 |

## 8. Alertas

| Cenário | Condição | Ação |
|---|---|---|
| Consumer lag alto | Lag crescente por 3 min | Acionar SRE e escalar consumidores. |
| Redis degradado | Erros ou latência alta | Entrar em modo degradado. |
| Falsos positivos altos | Aumento abrupto por regra | Acionar antifraude e avaliar Kill Switch. |
| DLQ crescendo | Eventos não processados | Investigar schema, regra ou dependência. |
| Latência > SLA | p99 acima do objetivo | Investigar gargalo e capacidade. |

## 9. Dashboards sugeridos

### Dashboard Executivo

- TPS atual
- alertas por minuto
- latência fim-a-fim
- disponibilidade
- taxa de falso positivo

### Dashboard SRE

- consumer lag
- CPU/memória
- Redis latency
- Kafka publish latency
- erros por dependência
- DLQ

### Dashboard Antifraude

- regras mais acionadas
- alertas por severidade
- alertas por canal
- regras desativadas por Kill Switch
- evolução de falsos positivos
