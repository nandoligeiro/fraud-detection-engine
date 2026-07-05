# Code Structure

Este documento descreve a organização de pacotes da aplicação.

## Objetivo

Evitar que detalhes de infraestrutura contaminem o domínio e a camada de aplicação.

## Pacotes principais

```text
br.com.nandoligeiro.frauddetection
├── domain
├── application
└── infrastructure
```

## Domain

Contém o modelo de negócio e serviços de domínio.

Responsabilidades:

- entidades e agregados;
- value objects;
- regras de domínio;
- serviços de domínio;
- decisões puras de negócio.

Não deve depender de Spring, Kafka, Redis, PostgreSQL ou HTTP.

## Application

A camada de aplicação é organizada por feature/caso de uso.

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

- `transaction`: recebimento de transações, controle de processamento e publicação do evento transacional;
- `detection`: avaliação antifraude e decisão de geração de alerta;
- `port.in`: contratos de entrada da feature;
- `port.out`: contratos de saída da feature;
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

Essa organização mantém a arquitetura hexagonal mais explícita e evita pacotes genéricos grandes como `application.port.in` e `application.service` crescendo sem contexto.
