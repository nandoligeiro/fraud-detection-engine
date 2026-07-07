# Fraud Metrics

Este documento descreve as primeiras métricas de negócio e operação do motor antifraude.

## Objetivo

Depois de fechar Kafka, idempotência e auditoria, a próxima evolução é tornar o comportamento do motor observável.

Logs ajudam a investigar casos pontuais. Métricas ajudam a enxergar tendência, saúde e operação.

## Métricas adicionadas

### Decisões antifraude

```text
fraud.decisions
```

Tags:

```text
decision=normal|suspicious
reason=no-rules|no-triggered-rules
severity=LOW|MEDIUM|HIGH|CRITICAL
```

### Alertas criados

```text
fraud.alerts
```

Tags:

```text
severity=LOW|MEDIUM|HIGH|CRITICAL
```

### Regras acionadas

```text
fraud.triggered.rules
```

Registra a quantidade de regras acionadas por transação suspeita.

### Auditoria

```text
fraud.audit
```

Tags:

```text
result=success|failure
```

## Como consultar

As métricas são expostas pelo actuator prometheus:

```text
GET /actuator/prometheus
```

Exemplos esperados no formato Prometheus:

```text
fraud_decisions_total
fraud_alerts_total
fraud_audit_total
fraud_triggered_rules_count
```

## Por que isso importa

Essas métricas ajudam a responder perguntas operacionais:

- quantas transações estão sendo classificadas como suspeitas;
- qual severidade aparece mais;
- se a auditoria está falhando;
- se as regras estão disparando demais ou de menos.

## Frase para entrevista

> Depois de logs, DLQ, Redis e auditoria, adicionei métricas de negócio com Micrometer. Assim, além de saber que o fluxo executou, eu consigo observar tendências: decisões normais e suspeitas, alertas por severidade, quantidade de regras acionadas e sucesso ou falha da auditoria. Isso aproxima o MVP de uma operação SRE real.
