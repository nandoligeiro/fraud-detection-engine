# Security — Motor de Detecção de Transações Suspeitas

## 1. Objetivo

Definir os controles de segurança aplicáveis ao motor antifraude, cobrindo trânsito, repouso, autenticação, autorização, tratamento de dados sensíveis, logs e auditoria.

## 2. Princípios

- Menor privilégio.
- Zero trust entre serviços.
- Dados sensíveis fora do caminho crítico.
- Segurança por padrão.
- Auditoria de ações administrativas.
- Logs sem PII.

## 3. Segurança em trânsito

Toda comunicação deve utilizar TLS.

Para comunicação interna entre componentes críticos, a recomendação é mTLS.

Fluxos protegidos:

- API Gateway → Ingestion Service
- Ingestion Service → Kafka
- Fraud Engine → Redis
- Fraud Engine → Kafka
- Rule Admin API → PostgreSQL
- Notification Service → serviços externos

## 4. Autenticação e autorização

### APIs transacionais

- Autenticação entre serviços.
- Token ou credencial corporativa emitida por plataforma interna.
- Validação de origem autorizada.

### APIs administrativas

- OAuth2/JWT.
- RBAC por perfil.
- Separação de permissões:
  - leitura de regras;
  - criação de regras;
  - alteração de thresholds;
  - desativação via Kill Switch;
  - aprovação de mudança crítica.

## 5. LGPD e dados sensíveis

O motor não precisa manipular CPF, nome, telefone ou email no caminho crítico de decisão.

Eventos devem usar identificadores pseudonimizados:

- `accountId` pseudonimizado;
- `cardId` pseudonimizado;
- `customerId` pseudonimizado quando necessário.

Dados pessoais necessários para notificação devem ser resolvidos por um serviço especializado, fora do Fraud Engine.

## 6. Segurança em repouso

- Redis com criptografia em repouso quando disponível.
- PostgreSQL criptografado.
- Kafka com criptografia de disco no cluster.
- Segredos gerenciados por Secret Manager, Vault ou KMS corporativo.
- Nenhum segredo versionado no Git.

## 7. Logs seguros

Logs não devem conter:

- CPF;
- nome;
- email;
- telefone;
- número completo de cartão;
- payload bruto da transação quando contiver dados sensíveis.

Campos permitidos:

- `traceId`;
- `transactionId`;
- `accountHash`;
- `cardHash`;
- `ruleId`;
- `ruleVersion`;
- `alertId`;
- `decision`.

## 8. Auditoria administrativa

Toda alteração de regra deve registrar:

- usuário;
- perfil;
- data/hora;
- ação;
- valor anterior;
- valor novo;
- motivo;
- origem da requisição.

## 9. Kill Switch

O Kill Switch deve exigir permissão específica e registrar auditoria completa.

Uso esperado:

- falso positivo massivo;
- regra mal configurada;
- incidente operacional;
- contenção rápida de impacto ao cliente.

## 10. Ameaças consideradas

| Ameaça | Mitigação |
|---|---|
| Reenvio duplicado de transação | Idempotência por `transactionId`. |
| Vazamento de dados sensíveis em logs | Logging seguro e mascaramento. |
| Serviço não autorizado publicando evento | Autenticação e autorização entre serviços. |
| Alteração indevida de regra | RBAC, auditoria e aprovação. |
| Interceptação de tráfego interno | mTLS. |
| Segredo exposto em repositório | Secret Manager e validação em pipeline. |

## 11. Testes de segurança

- Validação de payload sem PII em logs.
- Testes de autorização em endpoints administrativos.
- Teste de rejeição de tokens inválidos.
- Testes de idempotência.
- Verificação de secrets no pipeline.
