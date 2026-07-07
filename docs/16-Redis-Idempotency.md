# Redis Idempotency

Este documento descreve a evolução da idempotência do MVP para uma abordagem distribuída.

## Problema

No MVP, o controle de duplicidade pode usar memória local.

Isso é suficiente para demonstrar o fluxo, mas não atende bem um cenário com múltiplos pods.

Em produção, duas instâncias diferentes podem receber a mesma transação em momentos próximos. Se cada pod controla duplicidade apenas em memória, o efeito duplicado pode acontecer.

## Solução

Adicionar um adapter Redis para a porta:

```text
TransactionProcessingGuardPort
```

A aplicação continua chamando:

```text
acquire(transactionId, ttl)
```

A implementação Redis usa operação atômica via Spring Data Redis:

```text
setIfAbsent(key, value, ttl)
```

## Configuração

Por padrão, o projeto continua usando memória local:

```text
FRAUD_IDEMPOTENCY_PROVIDER=memory
```

Para usar Redis:

```text
FRAUD_IDEMPOTENCY_PROVIDER=redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

## Por que isso importa

A entrega de eventos pode ser at-least-once, mas o efeito de negócio precisa ser effectively-once.

A idempotência distribuída evita que retries, duplicidades ou múltiplos pods gerem o mesmo efeito mais de uma vez.

## Frase para entrevista

> Depois de endurecer Kafka com tópicos explícitos, retry e DLQ, evoluí a idempotência. No MVP ela era local, mas em ambiente distribuído isso não é suficiente. Com Redis, eu passo a ter um guard compartilhado entre pods usando uma operação atômica. Assim assumo at-least-once delivery, mas busco efeito effectively-once.
