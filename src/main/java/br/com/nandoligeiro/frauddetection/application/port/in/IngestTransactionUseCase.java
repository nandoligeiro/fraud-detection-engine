package br.com.nandoligeiro.frauddetection.application.port.in;

import br.com.nandoligeiro.frauddetection.application.usecase.IngestTransactionCommand;
import br.com.nandoligeiro.frauddetection.application.usecase.IngestionResult;

public interface IngestTransactionUseCase {

    IngestionResult ingest(IngestTransactionCommand command);
}
