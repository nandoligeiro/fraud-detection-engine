# Testcontainers Integration Tests

Este documento descreve os primeiros testes de integração usando Testcontainers.

## Objetivo

Validar adapters de infraestrutura em dependências reais, sem depender de serviços locais previamente instalados.

## Testes adicionados

### Redis idempotency

Valida o adapter Redis de idempotência:

```text
RedisProcessingGuardAdapter
```

Cenário:

```text
primeira aquisição da transactionId -> true
segunda aquisição da mesma transactionId -> false
```

Isso prova o comportamento esperado para o guard distribuído.

### PostgreSQL audit

Valida o adapter JDBC de auditoria:

```text
JdbcAlertAuditAdapter
```

Cenário:

```text
cria tabela fraud_alert_audit
persiste um alerta suspeito
consulta a quantidade de registros gravados
```

Isso prova a persistência básica da decisão antifraude.

## Execução

Os testes rodam no build Maven:

```bash
mvn clean verify
```

Os testes usam:

```text
@Testcontainers(disabledWithoutDocker = true)
```

Quando Docker está disponível, os containers sobem durante o teste. Quando Docker não está disponível, os testes são ignorados em vez de quebrar o build local.

## Por que isso importa

Mocks ajudam em regra de negócio, mas adapters de infraestrutura precisam ser testados com dependências mais próximas do real.

Com Testcontainers, conseguimos validar Redis e PostgreSQL sem acoplar o projeto a um ambiente manual.

## Frase para entrevista

> Depois de evoluir Kafka, Redis, auditoria e métricas, adicionei testes de integração com Testcontainers. A ideia é validar os adapters de infraestrutura contra serviços reais e efêmeros, como Redis e PostgreSQL, sem depender de ambiente manual. Isso aumenta a confiança do build e aproxima a validação do comportamento produtivo.
