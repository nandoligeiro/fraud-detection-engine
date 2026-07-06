# Interview Story — Fraud Detection Engine

Este documento é um roteiro para explicar o case em entrevista contando uma história técnica, com motivação, decisões arquiteturais, integrações, MVP e evoluções.

## 1. Abertura curta

> O desafio era desenhar e implementar um motor de detecção de transações suspeitas em tempo real. A solução precisava receber eventos financeiros, aplicar regras de fraude, gerar alertas internos e externos, continuar operando diante de falhas auxiliares e estar preparada para alto volume, algo como 8k TPS em média e pico de 25k TPS.

A minha proposta foi não tratar isso como uma API síncrona que faz tudo no request. Eu separei a jornada em três blocos:

```text
Ingestão → Decisão antifraude → Publicação de alerta
```

A ideia central foi: **receber rápido, desacoplar por evento e decidir de forma explicável**.

---

## 2. Entendimento do problema

Antes de pensar em tecnologia, eu entendi o problema como uma jornada de risco.

Uma transação chega ao sistema com dados como:

- `transactionId`;
- `accountId`;
- `cardId`;
- valor;
- moeda;
- merchant;
- canal;
- localização;
- horário da ocorrência.

O motor precisa responder a perguntas como:

- essa transação é de alto valor?
- é uma compra internacional?
- é card-not-present?
- alguma regra determinística foi acionada?
- devo gerar alerta?
- como garanto que a mesma transação não gere efeito duplicado?

A partir disso, modelei o domínio em três subdomínios principais:

```text
domain.transaction → representa a transação e seus value objects
domain.rule        → representa regras e motor de avaliação
domain.fraud       → representa decisão, severidade e alerta
```

Essa separação me ajuda a explicar que transação, regra e alerta são conceitos diferentes, mesmo fazendo parte da mesma jornada.

---

## 3. Decisão arquitetural principal

A arquitetura escolhida foi uma combinação de:

```text
Hexagonal Architecture
Clean Architecture
Package-by-feature
Event-driven architecture
```

O objetivo foi manter uma regra simples:

```text
Domain não conhece tecnologia.
Application orquestra caso de uso.
Infrastructure integra com mundo externo.
```

A estrutura final ficou assim:

```text
br.com.nandoligeiro.frauddetection
├── application
│   ├── transaction
│   └── detection
├── domain
│   ├── transaction
│   ├── fraud
│   └── rule
└── infrastructure
    ├── adapter
    └── config
```

A camada de domínio não depende de Spring, Kafka, Redis ou banco.

A aplicação depende do domínio e define portas.

A infraestrutura implementa adapters REST, Kafka, logging, memória e configurações.

Frase para usar:

> Eu quis deixar claro o limite entre decisão de negócio e detalhe técnico. A regra antifraude não deveria saber se a transação veio de REST, Kafka ou de um teste unitário.

---

## 4. Jornada de ingestão

O primeiro bloco é a ingestão.

```text
Client
  → REST TransactionController
  → TransactionIngestionService
  → TransactionProcessingGuardPort
  → TransactionEventPublisherPort
  → Kafka transaction-events
```

A API REST recebe a transação, valida o payload e transforma em um comando de aplicação:

```text
IngestTransactionCommand
```

Depois, o caso de uso `TransactionIngestionService` faz duas coisas:

1. controla duplicidade;
2. publica a transação para processamento assíncrono.

No MVP, o controle de duplicidade é simples, usando implementação em memória.

A evolução natural é Redis, com:

```text
SET key value NX EX ttl
```

Isso permite idempotência distribuída entre pods.

Frase para usar:

> Eu não tentei vender exatamente-once. Em sistemas distribuídos, preferi assumir at-least-once delivery e garantir efeito effectively-once com idempotência.

---

## 5. Por que Kafka no meio?

A decisão de usar Kafka foi para desacoplar o recebimento da transação da decisão antifraude.

Se eu fizer tudo dentro do request HTTP, qualquer lentidão no motor de regras, em banco, cache ou notificação pode afetar diretamente o canal de entrada.

Com Kafka:

```text
REST recebe rápido
Kafka absorve variação de carga
Consumers escalam horizontalmente
Alertas podem ser processados de forma independente
```

O tópico principal é:

```text
transaction-events
```

Ele representa uma transação aceita para avaliação.

Ponto importante:

> Kafka aqui não é só tecnologia de mensageria. Ele é uma fronteira arquitetural entre ingestão e decisão.

---

## 6. Jornada de detecção

O segundo bloco é a decisão antifraude.

```text
Kafka transaction-events
  → KafkaTransactionEventConsumer
  → FraudDetectionService
  → RuleProviderPort
  → DeterministicRuleEngine
  → FraudAlertFactory
```

O consumer Kafka recebe o evento e transforma o payload externo de volta para o domínio:

```text
TransactionEventPayload → Transaction
```

Depois chama o caso de uso:

```text
FraudDetectionService
```

Esse serviço carrega regras por uma porta:

```text
RuleProviderPort
```

No MVP, essa porta é implementada pelo:

```text
DefaultRuleProviderAdapter
```

Ele fornece regras determinísticas básicas:

```text
HIGH_AMOUNT
INTERNATIONAL_CARD_NOT_PRESENT
```

Depois, o domínio avalia a transação com:

```text
DeterministicRuleEngine
```

Se alguma regra for acionada, o domínio cria um alerta com:

```text
FraudAlertFactory
```

Frase para usar:

> Eu comecei com regras determinísticas porque elas são explicáveis, testáveis e auditáveis. Em fraude, explicabilidade é importante para sustentação e investigação.

---

## 7. Jornada de alerta

O terceiro bloco é a publicação do alerta.

```text
FraudDetectionService
  → FraudAlertPublisherPort
  → KafkaFraudAlertPublisherAdapter
  → Kafka fraud-alerts
  → KafkaFraudAlertConsumer
```

Quando uma regra dispara, o sistema gera um `FraudAlert` com:

- `alertId`;
- `transactionId`;
- `accountId`;
- severidade;
- decisão;
- regras acionadas;
- data de criação.

Esse alerta é publicado no tópico:

```text
fraud-alerts
```

No MVP, criei também um consumer simples:

```text
KafkaFraudAlertConsumer
```

Ele simula entrega para canal interno, backoffice, push ou e-mail.

A ideia não é implementar um serviço real de notificação agora, mas provar que a jornada completa existe.

Frase para usar:

> O MVP fecha a jornada básica: a transação entra, é avaliada, gera alerta e esse alerta chega a um consumidor. A integração real com push, e-mail ou backoffice viria como outro adapter.

---

## 8. MVP demonstrável

O MVP atual fecha o caminho feliz:

```text
REST /v1/transactions
  → TransactionIngestionService
  → transaction-events
  → KafkaTransactionEventConsumer
  → FraudDetectionService
  → DefaultRuleProviderAdapter
  → DeterministicRuleEngine
  → FraudAlertFactory
  → fraud-alerts
  → KafkaFraudAlertConsumer
```

Para rodar localmente:

```bash
make up
make kafka-topics
make run-mvp
```

O `make run-mvp` habilita:

```bash
FRAUD_KAFKA_ENABLED=true
FRAUD_KAFKA_ALERT_PUBLISHER_ENABLED=true
FRAUD_KAFKA_ALERT_CONSUMER_ENABLED=true
FRAUD_RULES_DEFAULT_ENABLED=true
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Com isso eu consigo demonstrar:

- recebimento REST;
- publicação em Kafka;
- consumo assíncrono;
- avaliação de regras;
- criação de alerta;
- publicação do alerta;
- consumo do alerta.

---

## 9. O que foi intencionalmente deixado como evolução

Eu deixei algumas partes como evolução, não por esquecimento, mas para manter o MVP controlado.

### 9.1 Redis para idempotência distribuída

Hoje a idempotência básica está modelada por porta.

Evolução:

```text
RedisTransactionProcessingGuardAdapter
```

Usando:

```text
SET NX EX
```

Motivo:

- evitar duplicidade entre múltiplos pods;
- suportar retries;
- trabalhar melhor com at-least-once.

Frase:

> A porta já existe. Trocar memória por Redis vira mudança de adapter, não mudança de domínio.

### 9.2 PostgreSQL para regras e auditoria

Hoje o MVP usa regras default fixas.

Evolução:

```text
fraud_rules
fraud_alert_audit
```

A tabela de regras permitiria alterar thresholds, severidade, prioridade e status.

A tabela de auditoria permitiria rastrear:

- qual regra disparou;
- qual versão da regra;
- qual severidade;
- qual decisão;
- quando o alerta foi criado.

Frase:

> Para produção, eu persistiria tanto as regras quanto a decisão tomada. Em fraude, decisão sem auditoria é difícil de sustentar.

### 9.3 Rule Admin API

Para cumprir alteração de regra sem redeploy, a evolução seria:

```text
POST   /v1/rules
GET    /v1/rules
PUT    /v1/rules/{ruleId}
PATCH  /v1/rules/{ruleId}/disable
```

Isso permitiria:

- criar regra;
- alterar threshold;
- desligar regra;
- versionar regra;
- criar kill switch.

Frase:

> Eu começaria com Rule Admin API simples e versionada, porque regra de fraude muda com frequência e não deveria exigir redeploy.

### 9.4 DLQ e retry no Kafka

O MVP cobre o fluxo feliz.

Para produção, eu adicionaria:

```text
DefaultErrorHandler
DeadLetterPublishingRecoverer
fraud-dlq
```

Motivo:

- erro técnico não pode travar partição indefinidamente;
- evento inválido precisa ir para quarentena;
- time de sustentação precisa reprocessar com segurança.

Frase:

> Em evento, o erro precisa ser tratado como parte do desenho. DLQ não é detalhe, é mecanismo operacional.

### 9.5 Observabilidade

Hoje temos actuator, métricas base e logs.

Evolução:

- OpenTelemetry;
- trace REST → Kafka → consumer → alerta;
- métricas de regra disparada;
- consumer lag;
- latência de detecção;
- p95/p99;
- dashboard SRE.

Métricas que eu criaria:

```text
fraud_transactions_received_total
fraud_transactions_evaluated_total
fraud_alerts_created_total
fraud_rule_triggered_total
fraud_detection_latency_ms
kafka_consumer_lag
fraud_dependency_error_total
```

Frase:

> Eu não olharia só CPU e memória. Para esse domínio, métricas de negócio também são essenciais: quantas transações avaliadas, quantos alertas criados, quais regras mais disparam e qual a latência da decisão.

### 9.6 Testcontainers

Para produção, eu adicionaria testes de integração com:

- Kafka;
- Redis;
- PostgreSQL.

A prioridade seria:

```text
Kafka flow test
Redis idempotency test
PostgreSQL audit/rules test
```

Frase:

> Teste unitário valida regra. Teste de integração valida contrato com infraestrutura. Os dois são necessários.

---

## 10. Trade-offs assumidos

### Síncrono vs assíncrono

Optei por assíncrono porque o desafio fala em alto volume e alertas em tempo real.

Trade-off:

- ganho desacoplamento e escala;
- perco simplicidade do request único;
- preciso cuidar de idempotência, lag, DLQ e observabilidade.

### Regras determinísticas vs ML

Optei por regras determinísticas no MVP.

Trade-off:

- ganho explicabilidade;
- ganho auditabilidade;
- perco sofisticação inicial;
- abro caminho para score híbrido no futuro.

### In-memory vs Redis

Usei memória para MVP.

Trade-off:

- simples para demonstrar;
- não serve para múltiplos pods;
- a porta permite trocar por Redis depois.

### Default rules vs banco

Usei regras default por configuração.

Trade-off:

- fecha fluxo rapidamente;
- não resolve governança de regra;
- evolução natural é PostgreSQL + Rule Admin API.

---

## 11. Como explicar a arquitetura em 2 minutos

> Eu dividi a solução em ingestão, detecção e alerta. A entrada REST recebe a transação e publica um evento em Kafka para não prender o canal de entrada na decisão antifraude. O consumer lê `transaction-events`, reconstrói o domínio e chama o caso de uso de detecção. A detecção carrega regras por uma porta, avalia no motor determinístico de domínio e, se alguma regra dispara, cria um `FraudAlert`. Esse alerta é publicado no tópico `fraud-alerts`, onde outro consumer simula o envio para canais internos ou externos.
>
> Usei arquitetura hexagonal para isolar domínio de tecnologia. Kafka, REST e configuração ficam em `infrastructure`; os casos de uso ficam em `application`; e as regras, transação e alerta ficam em `domain`. O MVP fecha o fluxo feliz. Para produção, eu evoluiria Redis para idempotência distribuída, PostgreSQL para regras e auditoria, DLQ para falhas Kafka, Rule Admin API para alteração sem redeploy e OpenTelemetry para rastreabilidade.

---

## 12. Como explicar em 30 segundos

> A solução é um motor antifraude event-driven. A API recebe transações, publica em Kafka, um consumer executa regras determinísticas no domínio e, quando há suspeita, publica um alerta em outro tópico. Organizei com arquitetura hexagonal: domínio puro, aplicação com casos de uso e infraestrutura com REST/Kafka. O MVP fecha o fluxo ponta a ponta; as evoluções naturais são Redis para idempotência, PostgreSQL para regras e auditoria, DLQ para resiliência e OpenTelemetry para observabilidade.

---

## 13. Fechamento forte

> Eu não tentei implementar tudo de produção no primeiro passo. Minha prioridade foi construir uma base arquitetural coerente, com separação clara de responsabilidades, fluxo ponta a ponta demonstrável e pontos de evolução explícitos. Assim, o MVP é simples o suficiente para validar a solução, mas estruturado o suficiente para crescer sem virar acoplamento entre API, regra, Kafka e banco.
