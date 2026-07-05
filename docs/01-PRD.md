# PRD — Motor de Detecção de Transações Suspeitas em Tempo Real

## 1. Visão geral

Este produto é um módulo de segurança para identificar transações financeiras suspeitas em tempo real, gerar alertas automáticos para canais internos de antifraude e acionar notificações para o cliente final.

A primeira versão será baseada em um **Rules Engine Determinístico**, sem uso de Machine Learning no caminho crítico. A arquitetura, porém, será preparada para evoluir futuramente para score híbrido e modelos preditivos.

## 2. Objetivos de negócio

- Reduzir perdas causadas por fraude.
- Aumentar velocidade de detecção de comportamento suspeito.
- Melhorar capacidade operacional do time antifraude.
- Permitir reação rápida a novas ondas de fraude via atualização dinâmica de regras.
- Preservar experiência do cliente legítimo, evitando bloqueios indevidos.

## 3. Objetivos técnicos

- Processar média de **8.000 TPS**.
- Suportar picos de **25.000 TPS**.
- Gerar alertas em até **500 ms** após ingestão do evento.
- Garantir entrega at-least-once com efeito **effectively-once** via idempotência.
- Permitir atualização de regras sem redeploy.
- Garantir observabilidade, segurança e resiliência desde a primeira versão.

## 4. Personas e atores

| Persona / Ator | Necessidade |
|---|---|
| Cliente final | Ser notificado rapidamente sobre transação suspeita sem atrito indevido. |
| Analista antifraude | Criar, alterar, desativar e auditar regras. |
| SRE | Monitorar disponibilidade, latência, throughput e dependências. |
| Sistema origem | Publicar eventos de transação de forma segura e resiliente. |
| Backoffice antifraude | Consumir alertas para investigação operacional. |

## 5. Escopo

### Dentro do escopo

- Ingestão de transações via REST e/ou Kafka.
- Validação do contrato da transação.
- Idempotência por `transactionId`.
- Execução de regras determinísticas.
- Regras stateless e stateful com janela deslizante.
- Publicação de alertas em tópico Kafka.
- Integração assíncrona com notificações.
- Observabilidade com métricas, logs e traces.
- Estratégia de fallback e degradação.

### Fora do escopo da V1

- Machine Learning no caminho crítico.
- Bloqueio automático definitivo da transação.
- Investigação automática de fraude.
- Dashboard analítico avançado.
- Treinamento de modelos preditivos.
- Motor de recomendação.

## 6. Requisitos funcionais

| ID | Título | Descrição | Prioridade |
|---|---|---|---|
| RF-01 | Ingestão de transações | Receber eventos de transações financeiras de sistemas internos. | Crítica |
| RF-02 | Avaliação determinística | Avaliar transações contra regras parametrizáveis e versionadas. | Crítica |
| RF-03 | Sliding Window | Manter histórico recente para regras de frequência, valor acumulado e geolocalização. | Crítica |
| RF-04 | Atualização dinâmica | Permitir criar, alterar, ativar, desativar e versionar regras sem redeploy. | Alta |
| RF-05 | Geração de alertas | Publicar alerta interno e acionar serviço de notificação externo. | Crítica |
| RF-06 | Idempotência | Evitar reprocessamento e alertas duplicados por `transactionId`. | Crítica |
| RF-07 | Degradação controlada | Continuar operando parcialmente quando dependências auxiliares falharem. | Alta |
| RF-08 | Kill Switch | Permitir desabilitar regra específica imediatamente em caso de falso positivo massivo. | Alta |
| RF-09 | Auditoria | Registrar regra, versão, decisão, autor da alteração e timestamps. | Alta |

## 7. Requisitos não funcionais

| ID | Categoria | Meta |
|---|---|---|
| RNF-01 | Throughput | Média 8.000 TPS, pico 25.000 TPS. |
| RNF-02 | Latência | SLA fim-a-fim de até 500 ms. Meta interna: p95 < 100 ms e p99 < 200 ms. |
| RNF-03 | Disponibilidade | 99,95% como meta inicial da V1. |
| RNF-04 | Idempotência | At-least-once delivery com efeito effectively-once. |
| RNF-05 | LGPD | Pseudonimização e não exposição de PII no Kafka e nos logs. |
| RNF-06 | Segurança em trânsito | TLS e mTLS entre serviços. |
| RNF-07 | Segurança em repouso | Criptografia com KMS para dados persistidos e temporários. |
| RNF-08 | Observabilidade | Métricas, logs estruturados e tracing distribuído. |
| RNF-09 | Manutenibilidade | Regras versionadas, testáveis e auditáveis. |

## 8. KPIs de sucesso

| KPI | Meta |
|---|---|
| Tempo até alerta | ≤ 500 ms |
| Alertas duplicados | 0 |
| Perda de eventos | 0 |
| Tempo para publicar nova regra | < 1 min |
| Tempo para rollback de regra | < 1 min |
| Consumer lag sob pico | Estável e decrescente após autoscaling |
| Tempo de desativação por Kill Switch | < 1 s |

## 9. Assunções

- `transactionId` é único e gerado pelo sistema origem.
- Kafka corporativo está disponível.
- Redis Cluster está disponível com alta disponibilidade.
- Serviço de notificação já existe.
- Infra Kubernetes está disponível.
- Sistemas origem conseguem publicar eventos no contrato acordado.

## 10. Restrições

- Não aumentar latência do fluxo principal de autorização.
- Não armazenar CPF, nome ou dados pessoais limpos no caminho crítico.
- Toda comunicação interna deve ser autenticada e criptografada.
- Todos os eventos e decisões devem ser auditáveis.

## 11. Critérios de aceite

- Uma transação válida deve ser ingerida e publicada no tópico `transaction-events`.
- Uma transação duplicada deve ser reconhecida e não gerar alerta duplicado.
- Uma regra deve ser criada, propagada e aplicada sem redeploy.
- Uma regra deve poder ser desativada via Kill Switch.
- Um alerta deve ser publicado em `fraud-alerts` em até 500 ms.
- Métricas de TPS, latência, consumer lag e erros devem estar disponíveis.
- Logs devem conter `traceId`, `transactionId`, `ruleId` e `alertId` quando aplicável.

## 12. Roadmap resumido

- V1: Rules Engine determinístico.
- V2: DSL de regras mais rica e score determinístico.
- V3: Risk Score híbrido.
- V4: Machine Learning como componente auxiliar.
- V5: Adaptive Fraud Detection.
