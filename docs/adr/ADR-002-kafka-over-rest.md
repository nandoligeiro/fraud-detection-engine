# ADR-002 — Kafka para Processamento Assíncrono em vez de REST Síncrono

## Status

Aceita

## Contexto

O sistema precisa suportar média de 8.000 TPS, picos de 25.000 TPS e gerar alertas em até 500 ms.

Chamadas síncronas entre todos os componentes tornariam o fluxo mais frágil: uma falha no serviço de notificação ou backoffice poderia impactar diretamente a detecção.

## Decisão

Utilizar **Apache Kafka** como backbone de eventos para transações, alertas, atualizações de regras e DLQ.

## Justificativa

Kafka oferece:

- alta vazão;
- particionamento;
- replay;
- persistência de eventos;
- consumer groups;
- desacoplamento temporal;
- melhor tolerância a picos.

## Alternativas consideradas

### REST síncrono

Mais simples de implementar, mas menos resiliente a picos e falhas transitórias.

### RabbitMQ

Excelente para filas e roteamento, mas Kafka é mais adequado para streaming de alto volume, replay e particionamento por chave.

### SQS/SNS

Boa opção cloud-managed, mas Kafka oferece mais controle sobre particionamento, consumo ordenado por chave e replay no contexto do case.

## Consequências

- Necessidade de governança de tópicos.
- Necessidade de estratégia de schema evolution.
- Necessidade de monitorar consumer lag.
- Necessidade de idempotência por consumo at-least-once.

## Mitigações

- Schema Registry ou validação rígida de contratos.
- DLQ.
- Observabilidade.
- Consumer groups bem dimensionados.
- Chaveamento por `accountId` ou `cardId`.
