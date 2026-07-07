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

## Por que isso importa

Em um fluxo assíncrono, erro faz parte da arquitetura.

Uma mensagem inválida, uma falha temporária de infraestrutura ou uma exceção no consumer não pode travar uma partição indefinidamente nem sumir silenciosamente.

## Estratégia

### 1. Tópicos explícitos

Os tópicos deixam de depender apenas de auto-create do Kafka local.

Tópicos previstos:

```text
transaction-events
fraud-alerts
rule-updates
fraud-dlq
```

### 2. Retry controlado

Falhas transitórias devem ser tentadas poucas vezes antes de seguir para DLQ.

Exemplo:

```text
maxAttempts = 3
backoff = 1s
```

### 3. DLQ

Eventos que não puderem ser processados seguem para:

```text
fraud-dlq
```

### 4. Operação

A DLQ permite:

- investigar payload problemático;
- corrigir causa raiz;
- reprocessar com segurança;
- evitar perda silenciosa.

## Frase para entrevista

> Depois de fechar o MVP, a primeira evolução foi tratar erro como parte do desenho assíncrono. Kafka não pode ser só o caminho feliz. Eu preciso de tópicos explícitos, retry controlado, DLQ e uma política de reprocessamento para sustentar operação real.
