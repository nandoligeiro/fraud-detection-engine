# ADR-005 — Motor de Regras Determinístico antes de IA

## Status

Aceita

## Contexto

O motor precisa tomar decisões em tempo real, com baixa latência, alto volume, rastreabilidade e capacidade de explicar por que uma transação foi considerada suspeita.

Modelos de Machine Learning podem agregar valor no futuro, mas também trazem desafios de explicabilidade, treinamento, versionamento, drift, custo operacional e governança.

## Decisão

A V1 utilizará um **Rules Engine determinístico**, sem Machine Learning no caminho crítico.

As regras serão configuráveis, versionadas, auditáveis e atualizadas sem redeploy.

## Justificativa

Regras determinísticas favorecem:

- explicabilidade;
- auditoria;
- previsibilidade;
- baixa latência;
- facilidade de depuração;
- controle operacional por times antifraude.

## Estratégia de evolução

```text
V1: Rules Engine determinístico
V2: DSL avançada e score determinístico
V3: Risk Score híbrido
V4: Machine Learning auxiliar
V5: Adaptive Fraud Detection
```

## Consequências positivas

- Decisões explicáveis.
- Menor complexidade inicial.
- Menor risco operacional.
- Mais aderente a auditorias.
- Time antifraude consegue operar regras sem deploy.

## Consequências negativas

- Pode não detectar padrões inéditos.
- Exige manutenção ativa de regras.
- Pode gerar falsos positivos se regras forem agressivas.

## Mitigações

- Métrica de falsos positivos.
- Kill Switch por regra.
- Versionamento e rollback.
- Auditoria de alterações.
- Roadmap para Risk Score e ML.
