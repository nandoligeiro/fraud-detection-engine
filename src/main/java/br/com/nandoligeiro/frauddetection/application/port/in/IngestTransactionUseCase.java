package br.com.nandoligeiro.frauddetection.application.port.in;

public interface IngestTransactionUseCase {

    IngestionResult ingest(IngestTransactionCommand command);
}
