# Bruno Collection — Fraud Detection Engine

Coleção Bruno para validar o MVP básico de ponta a ponta.

## Como abrir

No Bruno:

1. Clique em `Open Collection`.
2. Selecione a pasta:

```text
bruno/fraud-detection-engine
```

3. Selecione o environment `local`.

## Como preparar o ambiente

Na raiz do projeto:

```bash
make up
make kafka-topics
make run-mvp
```

## Requests incluídas

```text
health/Health Check
transactions/01 Ingest Normal Transaction
transactions/02 Ingest High Value Transaction
transactions/03 Ingest International Remote Transaction
transactions/04 Ingest Duplicate Transaction
```

## Ordem recomendada

1. Execute `Health Check`.
2. Execute `01 Ingest Normal Transaction`.
3. Execute `02 Ingest High Value Transaction`.
4. Execute `03 Ingest International Remote Transaction`.
5. Execute `04 Ingest Duplicate Transaction`.

## O que observar nos logs

Com o MVP rodando, as transações suspeitas devem gerar logs no fluxo:

```text
transaction accepted for async processing
transaction evaluated
fraud alert ready for async publication
fraud alert delivered to notification simulator
```

## Observação

A request de duplicidade depende da execução prévia da request `01 Ingest Normal Transaction`, pois reutiliza o mesmo `transactionId`.
