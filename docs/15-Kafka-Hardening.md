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

O primeiro PR desta evolução adiciona configuração explícita dos tópicos via Spring Kafka.

Antes, o ambiente local podia depender de auto-create topic ou de script manual.

Agora, quando `fraud.kafka.enabled=true`, a aplicação declara os tópicos esperados:

```text
transaction-events  12 partitions
fraud-alerts        12 partitions
rule-updates        3 partitions
fraud-dlq           3 partitions
```

## Por que isso importa

Tópico é contrato operacional.

Se a aplicação depende de um tópico, esse tópico precisa estar claro no código, nos scripts e na documentação.

Isso evita ambientes inconsistentes e facilita a conversa sobre particionamento, escala de consumers e DLQ.

## Próximos passos

### Retry controlado

Falhas transitórias devem ser tentadas poucas vezes antes de seguir para DLQ.

Exemplo:

```text
maxAttempts = 3
backoff = 1s
```

### DLQ

Eventos que não puderem ser processados seguem para:

```text
fraud-dlq
```

### Operação

A DLQ permite:

- investigar payload problemático;
- corrigir causa raiz;
- reprocessar com segurança;
- evitar perda silenciosa.

## Frase para entrevista

> Depois de fechar o MVP, a primeira evolução foi tratar Kafka como parte operacional da arquitetura, não só como detalhe técnico. Comecei declarando explicitamente os tópicos e particionamento. Depois disso, a evolução natural é retry controlado, DLQ e política de reprocessamento.
