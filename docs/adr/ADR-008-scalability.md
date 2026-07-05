# ADR-008 — Escalabilidade Horizontal

## Status

Aceita

## Contexto

O motor precisa suportar alto volume de transações e picos de carga, mantendo a geração de alertas dentro do SLA de 500 ms.

Uma única instância não oferece capacidade nem resiliência suficientes. O sistema precisa distribuir carga entre múltiplos nós e preservar a ordem relativa de eventos do mesmo cliente ou cartão.

## Decisão

Adotar escalabilidade horizontal baseada em:

- particionamento Kafka;
- consumer groups;
- serviços stateless;
- Redis Cluster para estado recente;
- Kubernetes HPA;
- métricas de consumer lag e uso de recursos.

## Estratégia

### Kafka

O tópico `transaction-events` será particionado por `accountId` ou `cardId`, preservando a ordem relativa das transações do mesmo portador ou cartão.

A sugestão inicial é começar com **32 partições**, ajustáveis por teste de carga.

### Aplicação

O `Fraud Engine` será stateless. O estado recente ficará no Redis, permitindo adicionar ou remover réplicas sem perda de contexto local.

### Kubernetes

O HPA poderá escalar consumidores com base em:

- CPU;
- memória;
- consumer lag;
- taxa de processamento;
- latência interna.

## Consequências positivas

- Suporte a picos de carga.
- Escala independente por componente.
- Melhor isolamento de falhas.
- Melhor utilização de recursos.
- Suporte a estratégias de implantação progressiva.

## Consequências negativas

- Número de partições limita paralelismo máximo por consumer group.
- Chaves desbalanceadas podem concentrar carga.
- Redis pode se tornar gargalo em regras stateful.
- Autoscaling não é instantâneo.

## Mitigações

- Testes de carga com diferentes quantidades de partições.
- Monitoramento de distribuição de chaves por partição.
- TTL adequado no Redis.
- Separação de regras stateless e stateful.
- Limites de HPA bem configurados.
- DLQ para eventos problemáticos.
