# Bruno Collection

Abra a pasta `bruno/fraud-detection-engine` no Bruno e selecione o environment `local`.

Prepare o ambiente:

```bash
make up
make kafka-topics
make run-mvp
```

Requests:

```text
health/Health Check
transactions/01 Ingest Normal Transaction
transactions/02 Ingest High Value Transaction
transactions/03 Ingest International Remote Transaction
transactions/04 Ingest Duplicate Transaction
```

Execute a request 01 antes da request 04, pois a 04 reutiliza o mesmo `transactionId` para validar duplicidade.
