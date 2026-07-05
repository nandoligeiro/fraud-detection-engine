# ADR-007 — Segurança e Conformidade LGPD no Fluxo Antifraude

## Status

Aceita

## Contexto

O motor antifraude processa eventos financeiros sensíveis. Mesmo que o objetivo seja detectar fraude, o sistema não deve expor dados pessoais desnecessários no caminho crítico.

A arquitetura precisa garantir segurança de ponta a ponta, incluindo comunicação entre serviços, armazenamento, logs, mensageria e APIs administrativas.

## Decisão

Adotar segurança por camadas, com os seguintes princípios:

- criptografia em trânsito;
- criptografia em repouso;
- autenticação e autorização entre serviços;
- pseudonimização de identificadores;
- segregação entre plano transacional e plano administrativo;
- logs sem PII;
- auditoria de alterações operacionais.

## Estratégias

### Comunicação entre serviços

- TLS para tráfego externo.
- mTLS para comunicação interna entre serviços críticos.
- OAuth2/JWT para APIs administrativas.

### Dados sensíveis

- Eventos no Kafka devem trafegar com identificadores pseudonimizados.
- CPF, nome, email e telefone não devem aparecer em logs.
- Dados necessários para notificação devem ser buscados por serviço especializado e não replicados no motor.

### Armazenamento

- Redis e PostgreSQL devem usar criptografia em repouso.
- Chaves devem ser gerenciadas por KMS/HSM corporativo.
- Segredos não devem estar em variáveis versionadas ou imagens Docker.

### Auditoria

Alterações de regras devem registrar:

- autor;
- data/hora;
- motivo;
- versão anterior;
- versão nova;
- origem da alteração.

## Consequências positivas

- Redução de exposição de dados pessoais.
- Melhor aderência à LGPD.
- Maior rastreabilidade operacional.
- Menor impacto em caso de vazamento de logs ou eventos.
- Separação clara de responsabilidades.

## Consequências negativas

- Maior complexidade na integração entre serviços.
- Necessidade de gestão de certificados e rotação de segredos.
- Maior esforço de auditoria e governança.

## Mitigações

- Automatizar rotação de segredos.
- Usar service mesh ou plataforma corporativa para mTLS quando disponível.
- Criar contratos de evento sem PII.
- Aplicar testes de segurança e validação de logs.
