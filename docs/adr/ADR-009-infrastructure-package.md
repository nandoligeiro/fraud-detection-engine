# ADR-009 — Infrastructure Package

## Status

Accepted

## Context

O projeto usa uma organização inspirada em arquitetura hexagonal.

Antes deste ajuste, adapters e configurações estavam em pacotes de topo como `adapter` e `config`. Isso funcionava, mas deixava menos explícito que REST, Kafka, logging e configurações Spring são detalhes técnicos.

## Decision

Agrupar adapters, payloads de integração e configurações técnicas dentro de `infrastructure`.

```text
br.com.nandoligeiro.frauddetection
├── domain
├── application
└── infrastructure
    ├── adapter
    │   ├── in
    │   ├── out
    │   └── kafka
    └── config
```

## Consequences

- `domain` continua independente de frameworks.
- `application` continua dependendo de portas e domínio.
- REST, Kafka, logging, memória e futuras integrações ficam em `infrastructure`.
- A estrutura fica mais clara para leitura, manutenção e apresentação do case.

## Trade-offs

- Os nomes dos pacotes ficam mais longos.
- A fronteira entre código de negócio e tecnologia fica mais explícita.
