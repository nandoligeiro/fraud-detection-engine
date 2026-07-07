# Kafka Hardening

Este documento descreve a primeira evolução depois do MVP.

## Objetivo

Sair do fluxo feliz e preparar o caminho para operação real com Kafka.

O MVP prova:

```text
REST -> transaction-events -> detection -> fraud-alerts -> notification simulator
```

A evolução de hardening adiciona:

```text
explicit topics
retry controlado
DLQ
política de reprocessamento
```

## Passo 1 — tópicos explícitos

Quando `fraud.kafka.enabled=true`, a aplicação declara os tópicos esperados:

```text
transaction-events  12 partitions
fraud-alerts        12 partitions
rule-updates        3 partitions
fraud-dlq           3 partitions
```

Tópico é contrato operacional. Se a aplicação depende de um tópico, esse tópico precisa estar claro no código, nos scripts e na documentação.

## Passo 2 — retry e DLQ

O consumer de `transaction-events` passa a usar um error handler explícito:

```text
DefaultErrorHandler
DeadLetterPublishingRecoverer
FixedBackOff
```

Configuração padrão:

```text
maxAttempts = 3
backoff = 1000ms
```

Variáveis:

```text
FRAUD_KAFKA_RETRY_MAX_ATTEMPTS
FRAUD_KAFKA_RETRY_BACKOFF_MS
```

Quando uma mensagem falha mesmo depois das tentativas, ela é enviada para:

```text
fraud-dlq
```

## Por que isso importa

Em fluxo assíncrono, erro faz parte da arquitetura.

Uma mensagem inválida, uma falha temporária ou uma exceção no consumer não pode travar uma partição indefinidamente nem sumir silenciosamente.

## Operação

A DLQ permite:

- investigar payload problemático;
- corrigir causa raiz;
- reprocessar com segurança;
- evitar perda silenciosa.

## Próximos passos

- Adicionar headers de erro na DLQ.
- Criar consumer administrativo de reprocessamento.
- Criar métrica de envio para DLQ.
- Criar alerta operacional para crescimento da DLQ.

## Frase para entrevista

> Depois de fechar o MVP, tratei Kafka como parte operacional da arquitetura. Primeiro declarei explicitamente os tópicos e o particionamento. Depois adicionei retry controlado e DLQ para que falhas no consumer não travem o fluxo nem desapareçam silenciosamente. Isso prepara a solução para sustentação e reprocessamento seguro.
