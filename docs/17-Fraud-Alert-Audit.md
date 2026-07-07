# Fraud Alert Audit

Este documento descreve a persistência de auditoria dos alertas antifraude.

## Problema

O MVP gera alertas e publica eventos, mas a decisão precisa ser rastreável.

Para sustentar investigação, suporte e explicabilidade, cada alerta suspeito precisa deixar um registro consultável.

## Solução

Foi adicionada uma porta de saída:

```text
FraudAlertAuditPort
```

O serviço de detecção cria o alerta, tenta persistir a auditoria e depois publica o alerta.

A persistência é tolerante a falha: se a auditoria falhar, o fluxo de alerta continua e o erro é registrado em log.

## Configuração

Por padrão, a auditoria fica desabilitada:

```text
FRAUD_AUDIT_ENABLED=false
```

Para habilitar:

```text
FRAUD_AUDIT_ENABLED=true
FRAUD_AUDIT_JDBC_URL=jdbc:postgresql://localhost:5432/fraud
FRAUD_AUDIT_USERNAME=fraud
FRAUD_AUDIT_PASSWORD=fraud
```

## Tabela

Script:

```text
scripts/create-alert-audit-table.sql
```

Campos principais:

```text
alert_id
transaction_id
account_id
decision
severity
triggered_rules
created_at
```

## Por que isso importa

A auditoria conecta a decisão técnica ao contexto operacional.

Sem esse registro, a solução detecta e alerta, mas fica fraca para investigação, suporte, compliance e melhoria das regras.

## Frase para entrevista

> Depois de resolver Kafka e idempotência distribuída, evoluí a rastreabilidade da decisão. Cada alerta suspeito passa a poder ser auditado em PostgreSQL, com decisão, severidade, regras acionadas e horário de criação. Mantive a auditoria fora do domínio e tolerante a falha para não bloquear a emissão do alerta.
