package br.com.nandoligeiro.frauddetection.application.transaction.port.in;

public interface TransactionIngestionUseCase {

    IngestionResult ingest(IngestTransactionCommand command);
}
