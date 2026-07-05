# Code Structure

Este documento descreve a organização de pacotes da aplicação.

## Objetivo

Evitar que detalhes de infraestrutura contaminem o domínio e a camada de aplicação, mantendo os pacotes organizados por feature/subdomínio.

## Pacotes principais

```text
br.com.nandoligeiro.frauddetection
├── domain
├── application
└── infrastructure
```

## Domain

O domínio é organizado por feature/subdomínio.

```text
domain
├── transaction
│   └── model
│       └── vo
├── fraud
│   ├── model
│   └── service
└── rule
    ├── model
    └── service
```

Responsabilidades:

- `transaction`: transação, canal e value objects transacionais;
- `fraud`: alerta, decisão, severidade e factory de alerta;
- `rule`: contrato de regra, regras determinísticas e motor de avaliação.

O domínio não deve depender de Spring, Kafka, Redis, PostgreSQL ou HTTP.

## Application

A aplicação também é organizada por feature.

```text
application
├── transaction
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
└── detection
    ├── port
    │   ├── in
    │   └── out
    └── service
```

Responsabilidades:

- `transaction`: caso de uso de recebimento e publicação de evento transacional;
- `detection`: caso de uso de avaliação antifraude e publicação de alerta;
- `port.in`: contratos de entrada da feature;
- `port.out`: dependências externas da feature;
- `service`: orquestração do caso de uso.

A camada de aplicação depende do domínio, mas não depende diretamente de adapters.

## Infrastructure

Contém detalhes técnicos e integrações externas.

```text
infrastructure
├── adapter
│   ├── in
│   │   ├── rest
│   │   └── kafka
│   ├── out
│   │   ├── kafka
│   │   ├── logging
│   │   ├── memory
│   │   └── rule
│   └── kafka
└── config
```

Responsabilidades:

- controllers REST;
- consumers Kafka;
- producers Kafka;
- mappers de payload externo;
- adapters de saída;
- configurações Spring;
- configurações Kafka;
- integrações com Redis/PostgreSQL no futuro.

## Regra prática

- `domain`: sabe negócio.
- `application`: sabe caso de uso.
- `infrastructure`: sabe tecnologia.

Essa organização mantém a arquitetura hexagonal mais explícita e evita pacotes genéricos grandes como `application.port.in` e `domain.model` crescendo sem contexto.
