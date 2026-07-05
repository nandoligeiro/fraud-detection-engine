# Scalability — Motor de Detecção de Transações Suspeitas

## 1. Objetivo

Descrever como a arquitetura suporta alto volume de transações, picos de carga e crescimento horizontal sem comprometer o SLA de geração de alertas em até 500 ms.

## 2. Estratégia geral

A escalabilidade é baseada em:

- Kafka para desacoplamento e particionamento;
- consumer groups para paralelismo;
- Fraud Engine stateless;
- Redis Cluster para estado recente;
- Kubernetes HPA para escala horizontal;
- observabilidade para ajuste contínuo.

## 3. Kafka e particionamento

O tópico `transaction-events` deve ser particionado por `accountId` ou `cardId`.

Essa decisão preserva a ordem relativa de transações do mesmo portador ou cartão, o que é importante para regras de sequência e frequência.

### Configuração inicial sugerida

| Item | Valor inicial |
|---|---|
| Partições `transaction-events` | 32 |
| Replication factor | 3 |
| Chave | `accountId` ou `cardId` |
| Semântica | At-least-once |

A quantidade de partições deve ser validada com teste de carga.

## 4. Consumer Groups

O `Fraud Engine` consome eventos como um consumer group.

O paralelismo efetivo é limitado pelo número de partições. Com 32 partições, até 32 consumidores podem processar em paralelo dentro do mesmo grupo.

## 5. Serviços stateless

Os serviços principais não devem manter estado local crítico.

Estado recente fica no Redis. Estado de auditoria fica no PostgreSQL. Eventos ficam no Kafka.

Isso permite:

- adicionar réplicas rapidamente;
- substituir pods sem perda de estado;
- executar rolling update;
- isolar falhas.

## 6. Redis Cluster

Redis é usado para:

- idempotência;
- sliding window;
- contadores temporários;
- última localização conhecida;
- snapshot operacional de regras.

Boas práticas:

- TTL por tipo de chave;
- separar prefixos por responsabilidade;
- evitar payloads grandes;
- monitorar latência;
- monitorar evictions;
- dimensionar memória com folga.

## 7. Autoscaling

O HPA deve considerar mais do que CPU.

Métricas recomendadas:

- `kafka_consumer_lag`;
- `transactions_per_second`;
- CPU;
- memória;
- latência interna;
- tempo médio de execução de regra.

## 8. Gargalos esperados

| Gargalo | Sintoma | Mitigação |
|---|---|---|
| Kafka com poucas partições | Consumer lag persistente | Aumentar partições e consumidores. |
| Chave desbalanceada | Algumas partições mais lentas | Avaliar estratégia de chaveamento. |
| Redis saturado | Latência alta em regras stateful | Otimizar estruturas e TTL. |
| Regras complexas demais | p99 alto | Profiling, limites e priorização. |
| Notification lento | Alertas atrasados | Processamento assíncrono e DLQ. |

## 9. Capacidade e testes

A capacidade deve ser validada com testes progressivos:

- carga média;
- pico;
- stress;
- soak test;
- falha de dependência;
- reprocessamento.

Métricas observadas:

- throughput;
- p95/p99;
- consumer lag;
- CPU;
- memória;
- latência Redis;
- tempo de publicação Kafka.

## 10. Degradação controlada

Quando uma dependência falha, o sistema deve operar parcialmente sempre que possível.

Exemplo:

- regras stateless continuam;
- regras stateful são marcadas como inconclusivas;
- eventos são encaminhados para reprocessamento;
- alertas operacionais são disparados.

## 11. Estratégia de evolução

- V1: 32 partições e escala horizontal básica.
- V2: HPA com métricas customizadas.
- V3: otimização de chaveamento e redução de skew.
- V4: separação de consumidores por criticidade de regra.
- V5: processamento especializado para regras de maior custo.
