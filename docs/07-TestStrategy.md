# Test Strategy — Motor de Detecção de Transações Suspeitas

## 1. Objetivo

Definir a estratégia de testes para validar corretude funcional, resiliência, idempotência, segurança, observabilidade e capacidade de processamento do motor antifraude.

## 2. Pirâmide de testes

```text
E2E / Carga / Caos
Integração / Contrato
Unitários
```

A base deve ser composta por testes unitários rápidos para o Rules Engine, complementados por testes de integração com Kafka, Redis e PostgreSQL usando Testcontainers.

## 3. Testes unitários

### Rules Engine

Validar:

- regra de alto valor;
- regra de frequência;
- regra de geolocalização;
- regra desabilitada;
- regra com versão específica;
- regra com prioridade;
- múltiplas regras acionadas;
- nenhum alerta gerado.

### Idempotência

Validar:

- primeira transação é aceita;
- transação duplicada é ignorada;
- alerta duplicado não é enviado;
- TTL expirado permite novo ciclo quando aplicável.

## 4. Testes de integração

Usar Testcontainers para:

- Kafka;
- Redis;
- PostgreSQL.

Cenários:

- publicar transação em `transaction-events`;
- consumir e gerar alerta em `fraud-alerts`;
- carregar regras do PostgreSQL;
- atualizar regra via evento `rule-updates`;
- verificar estado no Redis;
- enviar evento inválido para DLQ.

## 5. Testes de contrato

Validar OpenAPI:

- campos obrigatórios;
- tipos;
- formatos de data;
- resposta 202;
- resposta 400;
- resposta 409 para duplicidade.

Eventos Kafka também devem ter contrato validado:

- `transaction-events`;
- `fraud-alerts`;
- `rule-updates`.

## 6. Testes de carga

Ferramenta sugerida: k6.

Cenários:

| Teste | Objetivo |
|---|---|
| Load test | Validar carga média esperada. |
| Spike test | Validar pico súbito. |
| Stress test | Encontrar limite do sistema. |
| Soak test | Avaliar estabilidade por longo período. |

Métricas avaliadas:

- TPS;
- p95/p99;
- consumer lag;
- CPU;
- memória;
- latência Redis;
- latência Kafka;
- DLQ;
- erros.

## 7. Testes de resiliência

Cenários:

- Redis indisponível;
- PostgreSQL indisponível;
- Kafka com atraso;
- Notification Service indisponível;
- regra inválida publicada;
- aumento súbito de eventos duplicados;
- DLQ crescendo.

Comportamento esperado:

- modo degradado quando possível;
- DLQ para falhas não recuperáveis;
- alerta operacional;
- logs com rastreabilidade;
- ausência de duplicidade de alertas.

## 8. Testes de segurança

Validar:

- endpoints administrativos exigem autenticação;
- payloads inválidos são rejeitados;
- logs não possuem PII;
- tokens inválidos são rejeitados;
- alteração de regra gera auditoria;
- Kill Switch exige permissão adequada.

## 9. Testes de observabilidade

Validar:

- métricas expostas;
- trace propagado entre componentes;
- logs estruturados;
- alertas acionados em cenário de degradação;
- dashboard com sinais principais.

## 10. Critérios mínimos de aceite técnico

- Rules Engine coberto por testes unitários.
- Fluxo Kafka validado com Testcontainers.
- Idempotência validada com Redis.
- API validada contra OpenAPI.
- k6 executando cenário de carga.
- DLQ validada.
- Logs sem PII.
- Métricas operacionais expostas.
