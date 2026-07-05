# API — Motor de Detecção de Transações Suspeitas

## 1. Objetivo

Este documento descreve os contratos principais da solução, incluindo endpoints REST, eventos Kafka e padrões de erro.

A arquitetura suporta ingestão por REST e por eventos Kafka. Para o piloto, a API REST facilita demonstração, testes com Bruno e testes de carga com k6.

## 2. Princípios de contrato

- Contrato explícito via OpenAPI.
- Payloads sem dados pessoais desnecessários.
- Identificadores pseudonimizados quando possível.
- `transactionId` obrigatório para idempotência.
- `traceId` obrigatório para rastreabilidade.
- Versionamento por path: `/v1`.

## 3. Endpoints REST

### POST `/v1/transactions`

Recebe uma transação financeira para avaliação antifraude.

#### Request

```json
{
  "transactionId": "tx-123456",
  "accountId": "acc-hash-001",
  "cardId": "card-hash-001",
  "amount": 1250.90,
  "currency": "BRL",
  "merchantId": "merchant-001",
  "merchantCategoryCode": "5411",
  "channel": "CARD_PRESENT",
  "country": "BR",
  "city": "SAO_PAULO",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "occurredAt": "2026-07-05T10:15:30Z"
}
```

#### Headers

| Header | Obrigatório | Descrição |
|---|---|---|
| `X-Trace-Id` | Sim | Identificador de rastreabilidade. |
| `Idempotency-Key` | Sim | Chave idempotente, preferencialmente igual ao `transactionId`. |
| `Authorization` | Sim | Token de autenticação. |

#### Response 202

```json
{
  "transactionId": "tx-123456",
  "status": "ACCEPTED"
}
```

#### Response 409

Transação duplicada.

```json
{
  "transactionId": "tx-123456",
  "status": "DUPLICATED"
}
```

## 4. Endpoints operacionais

### GET `/actuator/health`

Health check técnico da aplicação.

### GET `/actuator/prometheus`

Exposição de métricas para Prometheus quando habilitado.

## 5. Eventos Kafka

### `transaction-events`

Evento produzido após validação e idempotência inicial.

```json
{
  "eventId": "evt-001",
  "eventType": "TRANSACTION_RECEIVED",
  "traceId": "trace-001",
  "transactionId": "tx-123456",
  "accountId": "acc-hash-001",
  "cardId": "card-hash-001",
  "amount": 1250.90,
  "currency": "BRL",
  "merchantId": "merchant-001",
  "merchantCategoryCode": "5411",
  "channel": "CARD_PRESENT",
  "country": "BR",
  "city": "SAO_PAULO",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "occurredAt": "2026-07-05T10:15:30Z",
  "receivedAt": "2026-07-05T10:15:30.100Z"
}
```

### `fraud-alerts`

Evento produzido quando uma ou mais regras são acionadas.

```json
{
  "alertId": "alert-001",
  "traceId": "trace-001",
  "transactionId": "tx-123456",
  "accountId": "acc-hash-001",
  "severity": "HIGH",
  "decision": "SUSPICIOUS",
  "triggeredRules": [
    {
      "ruleId": "HIGH_AMOUNT",
      "ruleVersion": 3,
      "reason": "Transaction amount greater than configured threshold"
    }
  ],
  "createdAt": "2026-07-05T10:15:30.210Z"
}
```

### `rule-updates`

Evento emitido quando uma regra é criada, alterada, ativada ou desativada.

```json
{
  "eventId": "rule-event-001",
  "ruleId": "HIGH_AMOUNT",
  "version": 4,
  "action": "UPDATED",
  "updatedBy": "anti-fraud-operator",
  "updatedAt": "2026-07-05T10:00:00Z"
}
```

## 6. Códigos de erro

| Código | Significado |
|---|---|
| 400 | Payload inválido. |
| 401 | Não autenticado. |
| 403 | Não autorizado. |
| 409 | Evento duplicado. |
| 429 | Limite de taxa excedido. |
| 500 | Erro interno. |
| 503 | Dependência indisponível. |

## 7. Versionamento

APIs REST usam `/v1` no path.

Eventos Kafka devem possuir `eventType`, `eventId` e campos compatíveis com evolução backward-compatible.
