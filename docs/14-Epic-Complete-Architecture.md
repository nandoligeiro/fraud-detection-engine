# Epic — Complete Architecture

Esta branch concentra a evolução da arquitetura completa do Fraud Detection Engine.

## Estratégia de branches

```text
main
  ↑
feature/epic-complete-architecture
  ↑
feature/<small-step>
```

A ideia é evoluir por PRs pequenos para a branch da Epic. Quando a Epic estiver em um ponto estável, abrimos ou atualizamos o PR da Epic para a `main`.

## Regras de trabalho

- Todo PR incremental deve apontar para `feature/epic-complete-architecture`.
- A branch da Epic também roda CI.
- A Epic só deve ir para `main` quando houver uma margem coerente de evolução.
- Cada PR pequeno deve ter escopo claro e demonstrável.

## Roadmap sugerido

### 1. Logs e evidência operacional

- Melhorar logs do fluxo assíncrono.
- Correlacionar `transactionId`, `eventId` e `alertId`.
- Documentar como validar a jornada.

### 2. k6 reports

- Exportar resumo JSON.
- Criar smoke test do MVP.
- Documentar o que k6 mede e o que não mede.

### 3. Kafka hardening

- Criar tópicos por configuração/script.
- Adicionar retry controlado.
- Adicionar DLQ.
- Documentar política de reprocessamento.

### 4. Redis idempotency

- Implementar adapter Redis.
- Usar `SET NX EX`.
- Validar duplicidade entre múltiplas instâncias.

### 5. PostgreSQL audit

- Persistir alertas gerados.
- Guardar decisão, severidade e regras acionadas.
- Preparar rastreabilidade da decisão.

### 6. Rule Admin API

- Criar endpoint administrativo de regras.
- Permitir habilitar, desabilitar e versionar regras.
- Preparar atualização sem redeploy.

### 7. Observabilidade

- Métricas de negócio.
- Métricas técnicas.
- OpenTelemetry.
- Dashboard SRE.

### 8. Testcontainers

- Kafka integration test.
- Redis integration test.
- PostgreSQL integration test.

## Frase para explicar

> Eu separei uma branch Epic para evoluir a arquitetura completa sem pressionar a main a cada pequeno passo. Cada melhoria entra como PR pequeno na Epic com CI. Quando a Epic estiver em um ponto consistente, ela é promovida para main por um PR maior, mantendo rastreabilidade e qualidade.
